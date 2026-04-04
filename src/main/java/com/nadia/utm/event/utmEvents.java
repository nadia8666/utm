package com.nadia.utm.event;

import com.nadia.utm.networking.TabLayerPayload;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.enchantment.utmEnchantments;
import com.nadia.utm.server.TabMenuServer;
import com.nadia.utm.util.OxyUtil;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

import static com.nadia.utm.registry.attachment.utmAttachments.ENTERED_2313AG;

@EventBusSubscriber(modid = "utm")
public class utmEvents {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 == 0) {
            refreshTabMenuData(event.getServer());
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        TabMenuServer.loadData(server);
        refreshTabMenuData(server);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        TabMenuServer.saveData();
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftServer server = event.getEntity().getServer();
        if (server == null) return;

        if (event.getEntity() instanceof ServerPlayer player) TabMenuServer.addPlayer(player);
        refreshTabMenuData(server);
    }

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) refreshTabMenuData(player.getServer());
    }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) refreshTabMenuData(player.getServer());

        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getTo().equals(utmDimensions.AG_KEY)) {
                if (!player.getData(ENTERED_2313AG)) {
                    player.setData(ENTERED_2313AG, true);
                    player.setRespawnPosition(utmDimensions.AG_KEY, player.blockPosition(), player.getYRot(), true, true);
                }
            }
        }
    }

    public static void refreshTabMenuData(MinecraftServer server) {
        TabLayerPayload payload = TabMenuServer.create(server);
        PacketDistributor.sendToAllPlayers(payload);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living && !entity.level().isClientSide) {
            if (entity.level().dimension().equals(utmDimensions.AG_KEY)) {
                var gravity = living.getAttribute(Attributes.GRAVITY);
                if (gravity != null && gravity.getBaseValue() != 0.12) {
                    gravity.setBaseValue(0.12);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        //TODO: see if this can be optimized better
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            MinecraftServer server = sPlayer.getServer();
            if (server == null) return;

            ServerLevel level = server.getLevel(utmDimensions.AG_KEY);
            boolean inAG = sPlayer.serverLevel().dimension().equals(utmDimensions.AG_KEY);
            boolean enteredAG = sPlayer.getData(ENTERED_2313AG);

            if (enteredAG && !inAG) {
                if (level != null) {
                    int x = sPlayer.blockPosition().getX();
                    int z = sPlayer.blockPosition().getZ();
                    int height = getSurface(level, x, z);

                    if (height == -13579) {
                        height = -63;
                        level.setBlock(new BlockPos(x, -64, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
                    }
                    sPlayer.teleportTo(level, sPlayer.getX(), height, sPlayer.getZ(), sPlayer.getYRot(), sPlayer.getXRot());
                }
            } else if (!enteredAG && inAG) {
                sPlayer.setData(ENTERED_2313AG, true);
                sPlayer.setRespawnPosition(utmDimensions.AG_KEY, sPlayer.blockPosition(), sPlayer.getYRot(), true, true);
            }

            if (!OxyUtil.canBreathe(sPlayer) && level != null && !sPlayer.getAbilities().instabuild) {
                ItemStack helmet = sPlayer.getItemBySlot(EquipmentSlot.HEAD);
                ItemStack chestplate = sPlayer.getItemBySlot(EquipmentSlot.CHEST);
                ItemStack leggings = sPlayer.getItemBySlot(EquipmentSlot.LEGS);
                ItemStack boots = sPlayer.getItemBySlot(EquipmentSlot.FEET);
                if (
                        (helmet.is(AllItems.NETHERITE_DIVING_HELMET) || helmet.is(AllItems.COPPER_DIVING_HELMET)) &&
                                !chestplate.isEmpty() &&
                                !leggings.isEmpty() &&
                                (boots.is(AllItems.NETHERITE_DIVING_BOOTS) || boots.is(AllItems.COPPER_DIVING_BOOTS)) &&
                                !BacktankUtil.getAllWithAir(sPlayer).isEmpty()
                ) {
                    List<ItemStack> tanks = BacktankUtil.getAllWithAir(sPlayer);
                    if (level.getGameTime() % 20 == 0) {
                        BacktankUtil.consumeAir(sPlayer, tanks.getFirst(), 1);

                        if (helmet.is(AllItems.COPPER_DIVING_HELMET))
                            helmet.setDamageValue(helmet.getDamageValue() + 1);

                        if (boots.is(AllItems.COPPER_DIVING_BOOTS))
                            boots.setDamageValue(boots.getDamageValue() + 1);
                    }
                } else {
                    sPlayer.hurt(level.damageSources().source(DamageTypes.IN_WALL), 1f);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
            int jumpPower = boots.getEnchantmentLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(utmEnchantments.POWER_JUMP));
            if (jumpPower > 0) {
                Vec3 delta = player.getDeltaMovement();
                player.setDeltaMovement(delta.x, delta.y + (0.10D * jumpPower), delta.z);
            }
        }
    }

    public static int getSurface(ServerLevel level, int x, int z) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, maxY, z);

        while (pos.getY() > minY) {
            if (!level.getBlockState(pos).isAir()) {
                return pos.above().getY();
            }
            pos.move(0, -1, 0);
        }

        return -13579;
    }
}
