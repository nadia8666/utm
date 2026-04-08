package com.nadia.utm.event;

import com.nadia.utm.networking.payloads.LaunchContraptionPayload;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.enchantment.utmEnchantments;
import com.nadia.utm.server.TabMenuServer;
import com.nadia.utm.util.AdvancementUtil;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.utm;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.nadia.utm.registry.attachment.utmAttachments.ENTERED_2313AG;

@ForceLoad
public class SpacePlayerStateHandler {
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

    public static void onPlayerTick(PlayerTickEvent.Post event) {
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

                    AdvancementUtil.AwardAdvancement(sPlayer, utm.key("2313ag/suffocate"));
                }
            }

            List<ItemStack> tanks = BacktankUtil.getAllWithAir(sPlayer);
            if (!tanks.isEmpty()) {
                double headLevel = sPlayer.getBoundingBox().maxY + 0.4;
                BlockPos pos = BlockPos.containing(sPlayer.getX(), headLevel, sPlayer.getZ());
                BlockState tank = sPlayer.level().getBlockState(pos);
                if (tank.getBlock() instanceof KineticBlock block) {
                    if (block.hasShaftTowards(sPlayer.level(), pos, tank, Direction.DOWN) && sPlayer.level().getBlockEntity(pos) instanceof KineticBlockEntity be) {
                        ItemStack target = tanks.getFirst();
                        int max = BacktankUtil.maxAir(target);
                        int air = BacktankUtil.getAir(target);
                        if (air < max) {
                            double strength = OxyUtil.getCollectionStrength(sPlayer.level(), pos);
                            float abs = Math.abs(be.getSpeed());
                            int increment = Mth.clamp(((int) abs - 100) / 20, 1, 5);
                            BacktankUtil.consumeAir(sPlayer, target, -Math.max(Mth.floor(increment * strength), 0));
                        }
                    }
                }
            }
        }
    }

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

    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) TabMenuServer.refresh(player.getServer());

        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getTo().equals(utmDimensions.AG_KEY)) {
                if (!player.getData(ENTERED_2313AG)) {
                    player.setData(ENTERED_2313AG, true);
                    player.setRespawnPosition(utmDimensions.AG_KEY, player.blockPosition(), player.getYRot(), true, true);
                }
            }
        }
    }

    static {
        utmEvents.register(PlayerTickEvent.Post.class, SpacePlayerStateHandler::onPlayerTick);
        utmEvents.register(EntityTickEvent.Post.class, SpacePlayerStateHandler::onEntityTick);
        utmEvents.register(LivingEvent.LivingJumpEvent.class, SpacePlayerStateHandler::onJump);
        utmEvents.register(PlayerEvent.PlayerChangedDimensionEvent.class, SpacePlayerStateHandler::onDimensionChange);
    }

    private static final Set<Block> UNMODIFIED_BLOCKS = Set.of(
            Blocks.STONE,
            Blocks.COBBLESTONE,
            Blocks.ANDESITE,
            Blocks.STONE_BRICKS,
            Blocks.TUFF,
            Blocks.DEEPSLATE,
            Blocks.COBBLED_DEEPSLATE
    );

    public static void launchRecieved(LaunchContraptionPayload payload, IPayloadContext context) {
        Player player = context.player();
        Entity vehicle = player.getVehicle();
        ServerLevel target = Objects.requireNonNull(Objects.requireNonNull(player.getServer()).getLevel(utmDimensions.AG_KEY));
        if (player.level().dimension().equals(utmDimensions.AG_KEY)) {
            if (vehicle instanceof AbstractContraptionEntity contraption)
                contraption.disassemble();

            return;
        }

        if (vehicle instanceof AbstractContraptionEntity contraption) {
            List<Entity> passengers = List.copyOf(contraption.getPassengers());

            AABB bounds = contraption.getBoundingBox();
            int minX = Mth.floor(bounds.minX);
            int maxX = Mth.ceil(bounds.maxX);
            int minZ = Mth.floor(bounds.minZ);
            int maxZ = Mth.ceil(bounds.maxZ);

            int highestHeight = -13579;
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    int surfaceY = SpacePlayerStateHandler.getSurface(target, x, z);
                    if (surfaceY > highestHeight) {
                        highestHeight = surfaceY;
                    }
                }
            }

            if (highestHeight == -13579) {
                highestHeight = -63;
                target.setBlock(new BlockPos(contraption.blockPosition().getX(), -64, contraption.blockPosition().getZ()), Blocks.COBBLESTONE.defaultBlockState(), 3);
            }

            double yOffset = Math.min(highestHeight, 319 - (bounds.getYsize())) - contraption.getY();

            AABB targetBounds = bounds.move(0, yOffset, 0);
            int tMinX = Mth.floor(targetBounds.minX);
            int tMaxX = Mth.ceil(targetBounds.maxX);
            int tMinY = Mth.floor(targetBounds.minY);
            int tMaxY = Mth.ceil(targetBounds.maxY);
            int tMinZ = Mth.floor(targetBounds.minZ);
            int tMaxZ = Mth.ceil(targetBounds.maxZ);

            BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
            for (int x = tMinX; x <= tMaxX; x++) {
                for (int y = tMinY; y <= tMaxY; y++) {
                    for (int z = tMinZ; z <= tMaxZ; z++) {
                        mPos.set(x, y, z);
                        BlockState state = target.getBlockState(mPos);

                        if (!state.isAir() && UNMODIFIED_BLOCKS.contains(state.getBlock()) && !state.hasBlockEntity()) {
                            target.setBlock(mPos, Blocks.AIR.defaultBlockState(), 3);
                        }

                        if (y == tMinY && target.getBlockState(mPos).isAir() && target.getBlockState(mPos.below()).isAir()) {
                            target.setBlock(mPos.below(), Blocks.COBBLESTONE.defaultBlockState(), 3);
                        }
                    }
                }
            }

            CompoundTag nbt = contraption.getContraption().writeNBT(contraption.registryAccess(), false);

            Entity cVehicle = contraption.getVehicle();
            AtomicReference<Entity> finalVehicle = new AtomicReference<>(null);
            contraption.getContraption().getBlocks().clear();
            contraption.changeDimension(new DimensionTransition(
                    target,
                    contraption.position().add(0, yOffset, 0),
                    contraption.getDeltaMovement(),
                    contraption.getYRot(),
                    contraption.getXRot(),
                    (newEntity) -> {
                        if (newEntity instanceof AbstractContraptionEntity newContraption) {

                            newContraption.getContraption().readNBT(target, nbt, true);
                            player.getServer().tell(new TickTask(player.getServer().getTickCount() + 20, () -> {
                                Entity targVehicle = finalVehicle.get();
                                if (targVehicle != null) {
                                    newContraption.startRiding(targVehicle);

                                    int index = 0;
                                    for (Entity pass : passengers) {
                                        newContraption.addSittingPassenger(pass, index);
                                        index++;
                                    }

                                    newContraption.getContraption().invalidateClientContraptionStructure();
                                    newContraption.getContraption().invalidateClientContraptionChildren();
                                    newContraption.getContraption().invalidateColliders();
                                    newContraption.getContraption().resetClientContraption();
                                } else {
                                    newContraption.stopRiding();
                                    newContraption.discard();
                                }

                                for (Entity pass : List.copyOf(newContraption.getPassengers())) {
                                    pass.stopRiding();
                                }
                            }));
                        }
                    }
            ));

            if (cVehicle != null) {
                cVehicle.changeDimension(new DimensionTransition(
                        target,
                        cVehicle.position().add(0, yOffset, 0),
                        cVehicle.getDeltaMovement(),
                        cVehicle.getYRot(),
                        cVehicle.getXRot(),
                        finalVehicle::set
                ));
            }
        }
    }
}
