package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.registry.enchantment.utmEnchantments;
import com.nadia.utm.registry.planets.utmPlanets;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.util.SableUtil;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Set;

@ForceLoad
public class SpaceStateHandler {
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            MinecraftServer server = sPlayer.getServer();
            if (server == null) return;

            Persistance.checkPersistance(sPlayer, sPlayer.getData(utmAttachments.REGISTERED_PLANET));
            Breathability.checkSuffocating(sPlayer);
        }
    }

    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide && entity instanceof LivingEntity living) {
            utmPlanets.Planet planet = utmPlanets.get(living.level());
            if (planet != null) {
                AttributeInstance gravity = living.getAttribute(Attributes.GRAVITY);
                if (gravity != null && gravity.getBaseValue() != planet.GRAVITY)
                    gravity.setBaseValue(planet.GRAVITY);
            }

            if (!(living instanceof Player))
                Breathability.checkSuffocatingEntity(living);
        }
    }

    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            int jumpPower = boots.getEnchantmentLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(utmEnchantments.POWER_JUMP));
            if (jumpPower > 0) {
                Vec3 delta = player.getDeltaMovement();
                player.setDeltaMovement(delta.x, delta.y + (0.10D * jumpPower), delta.z);
            }
        }
    }

    public static final Set<Block> UNMODIFIED_BLOCKS = Set.of(
            Blocks.STONE,
            Blocks.COBBLESTONE,
            Blocks.ANDESITE,
            Blocks.STONE_BRICKS,
            Blocks.TUFF,
            Blocks.DEEPSLATE,
            Blocks.COBBLED_DEEPSLATE
    );

    static {
        utmEvents.register(PlayerTickEvent.Post.class, SpaceStateHandler::onPlayerTick);
        utmEvents.register(EntityTickEvent.Post.class, SpaceStateHandler::onEntityTick);
        utmEvents.register(LivingEvent.LivingJumpEvent.class, SpaceStateHandler::onJump);
        utmEvents.register(LivingFallEvent.class, event -> {
            Entity entity = event.getEntity();

            if (entity.getTags().contains("utm_reentry_landing")) {
                event.setDamageMultiplier(0);
                event.setDistance(0);
                event.setCanceled(true);
                entity.removeTag("utm_reentry_landing");
            }
        });

        utmEvents.register(BonemealEvent.class, event -> {
            LevelAccessor accessor = event.getLevel();
            BlockPos pos = event.getPos();

            if (accessor instanceof Level level) {
                if (!OxyUtil.hasOxygen(level)) {
                    SubLevel sublevel = (SubLevel) SableCompanion.INSTANCE.getContaining(level, SableUtil.toVec(pos));
                    BlockPos controller;

                    if (sublevel != null)
                        controller = OxyUtil.isSealedLocalPos(sublevel, pos);
                    else
                        controller = OxyUtil.isSealed(level, pos);

                    if (controller == null)
                        event.setCanceled(false);
                }
            }
        });

        utmEvents.register(CropGrowEvent.Pre.class, event -> {
            LevelAccessor accessor = event.getLevel();
            BlockPos pos = event.getPos();

            if (accessor instanceof Level level) {
                if (!OxyUtil.hasOxygen(level)) {
                    SubLevel sublevel = (SubLevel) SableCompanion.INSTANCE.getContaining(level, SableUtil.toVec(pos));
                    BlockPos controller;

                    if (sublevel != null)
                        controller = OxyUtil.isSealedLocalPos(sublevel, pos);
                    else
                        controller = OxyUtil.isSealed(level, pos);

                    if (controller == null)
                        event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
                }
            }
        });
    }
}
