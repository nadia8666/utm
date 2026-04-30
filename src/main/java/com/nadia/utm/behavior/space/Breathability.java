package com.nadia.utm.behavior.space;

import com.nadia.utm.block.entity.AbstractSealerBlockEntity;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.util.AdvancementUtil;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.util.PosUtil;
import com.nadia.utm.util.SableUtil;
import com.nadia.utm.utm;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

@ForceLoad
class Breathability {
    public static void checkSuffocatingEntity(Entity entity) {
        SubLevel level = SableUtil.getSublevel(entity);
        BlockPos controller = null;
        if (level != null)
            controller = OxyUtil.isSealed(level, OxyUtil.getSealCheckPositions(entity).toArray(new BlockPos[0]));

        if (controller == null)
            controller = OxyUtil.isSealed(entity.level(), entity.blockPosition());

        boolean sealed = controller != null;
        boolean breathable = OxyUtil.canBreathe(entity);

        if (!breathable) {
            if (!sealed) {
                if (entity.hasData(utmAttachments.TEMPORARY_OXYGEN)) {
                    int forceOxygen = entity.getData(utmAttachments.TEMPORARY_OXYGEN);
                    if (forceOxygen > 0)
                        entity.setData(utmAttachments.TEMPORARY_OXYGEN, forceOxygen--);

                    if (forceOxygen <= 0)
                        entity.hurt(entity.level().damageSources().source(DamageTypes.IN_WALL), 1f);
                } else {
                    OxyUtil.giveTemporaryAir(entity, 5 * 60 * 20);
                }
            } else if (entity.hasData(utmAttachments.TEMPORARY_OXYGEN))
                entity.removeData(utmAttachments.TEMPORARY_OXYGEN);

        } else if (entity.hasData(utmAttachments.TEMPORARY_OXYGEN))
            entity.removeData(utmAttachments.TEMPORARY_OXYGEN);
    }

    public static void checkSuffocating(ServerPlayer sPlayer, boolean inAG) {
        SubLevel level = SableUtil.getSublevel(sPlayer);
        BlockPos controller = null;
        if (level != null)
            controller = OxyUtil.isSealed(level, OxyUtil.getSealCheckPositions(sPlayer).toArray(new BlockPos[0]));

        if (controller == null)
            controller = OxyUtil.isSealed(sPlayer.serverLevel(), sPlayer.blockPosition());

        boolean sealed = controller != null;
        int forceOxygen = sPlayer.getData(utmAttachments.TEMPORARY_OXYGEN);
        boolean breathable = OxyUtil.canBreathe(sPlayer);
        if (!breathable && !sealed && forceOxygen <= 0 && !sPlayer.getAbilities().instabuild) {
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
                if (sPlayer.serverLevel().getGameTime() % 20 == 0) {
                    BacktankUtil.consumeAir(sPlayer, tanks.getFirst(), 1);

                    if (helmet.is(AllItems.COPPER_DIVING_HELMET))
                        helmet.setDamageValue(helmet.getDamageValue() + 1);

                    if (boots.is(AllItems.COPPER_DIVING_BOOTS))
                        boots.setDamageValue(boots.getDamageValue() + 1);
                }
            } else {
                sPlayer.hurt(sPlayer.serverLevel().damageSources().source(DamageTypes.IN_WALL), 1f);

                if (inAG) AdvancementUtil.AwardAdvancement(sPlayer, utm.key("2313ag/suffocate"));
            }
        }

        if (forceOxygen > 0) {
            if (forceOxygen - 1 <= 0)
                sPlayer.removeData(utmAttachments.TEMPORARY_OXYGEN);
            else
                sPlayer.setData(utmAttachments.TEMPORARY_OXYGEN, forceOxygen - 1);
        }

        checkRefillBacktank(sPlayer, sealed, controller);

        // sometimes it will bug and not clear out the attached air blocks. this is the method
        if (sealed && sPlayer.level().getGameTime() % 20 == 0) {
            BlockEntity be = sPlayer.serverLevel().getBlockEntity(controller);
            if (level != null) {
                be = level.getLevel().getBlockEntity(controller);
            }

            if (!(be instanceof AbstractSealerBlockEntity) || (be instanceof AbstractSealerBlockEntity sealer && !sealer.ACTIVE)) {
                ServerLevel sLevel = sPlayer.serverLevel();
                Set<BlockPos> VISITED = new HashSet<>();
                Queue<BlockPos> QUEUE = new LinkedList<>(PosUtil.getAdjacent(controller));

                while (!QUEUE.isEmpty()) {
                    BlockPos current = QUEUE.poll();

                    if (VISITED.contains(current)) continue;
                    VISITED.add(current);

                    BlockPos otherController = OxyUtil.isSealed(sLevel, current);
                    if (controller.equals(otherController)) {
                        if (level != null) OxyUtil.setBlockSealed(level, current, null);
                        else OxyUtil.setBlockSealed(sLevel, current, null);
                        for (BlockPos.MutableBlockPos neighbor : PosUtil.forAdjacent(current))
                            if (!VISITED.contains(neighbor)) QUEUE.add(neighbor.immutable());
                    }
                }

                if (level != null) OxyUtil.setBlockSealed(level, controller, null);
                else OxyUtil.setBlockSealed(sLevel, controller, null);
            }
        }
    }

    public static void checkRefillBacktank(ServerPlayer sPlayer, boolean sealed, BlockPos controller) {
        List<ItemStack> tanks = OxyUtil.getAllBacktanks(sPlayer);
        if (!tanks.isEmpty()) {
            double headLevel = sPlayer.getBoundingBox().maxY + 0.4;
            BlockPos pos = BlockPos.containing(sPlayer.getX(), headLevel, sPlayer.getZ());
            SubLevel level = SableUtil.getSublevel(sPlayer);

            BlockState shaft = null;
            if (level != null) {
                pos = SableUtil.toLocalPos(level.logicalPose(), pos);
                shaft = SableUtil.getState(level, pos);
            }

            if (shaft == null)
                shaft = sPlayer.level().getBlockState(pos);

            if (shaft.getBlock() instanceof KineticBlock block) {
                if (block.hasShaftTowards(sPlayer.level(), pos, shaft, Direction.DOWN) && sPlayer.level().getBlockEntity(pos) instanceof KineticBlockEntity be) {
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

            if (sealed) {
                int ticks = 5;
                if (sPlayer.level().getBlockEntity(controller) instanceof AbstractSealerBlockEntity be) {
                    ticks = Math.clamp((be.SYNCED_VOLUME / be.getMaxVolume()) * 5L, 1, 5);
                }

                if (sPlayer.level().getGameTime() % ticks == 0)
                    BacktankUtil.consumeAir(sPlayer, tanks.getFirst(), -1);
            }
        }
    }
}