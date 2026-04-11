package com.nadia.utm.behavior.space;

import com.nadia.utm.block.entity.AbstractSealerBlockEntity;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.util.AdvancementUtil;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.utm;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

@ForceLoad
class Breathability {
    public static void checkSuffocating(ServerPlayer sPlayer, boolean inAG) {
        BlockPos controller = OxyUtil.isSealed(sPlayer.serverLevel(), sPlayer.blockPosition());
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

        if (forceOxygen > 0)
            sPlayer.setData(utmAttachments.TEMPORARY_OXYGEN, forceOxygen - 1);

        checkRefillBacktank(sPlayer, sealed, controller);

        // sometimes it will bug and not clear out the attached air blocks. this is the method
        if (sealed && sPlayer.level().getGameTime() % 20 == 0) {
            if (!(sPlayer.serverLevel().getBlockEntity(controller) instanceof AbstractSealerBlockEntity)) {
                ServerLevel sLevel = sPlayer.serverLevel();
                Set<BlockPos> VISITED = new HashSet<>();
                Queue<BlockPos> QUEUE = new LinkedList<>(AbstractSealerBlockEntity.getAdjacent(controller));

                while (!QUEUE.isEmpty()) {
                    BlockPos current = QUEUE.poll();

                    if (VISITED.contains(current)) continue;
                    VISITED.add(current);

                    BlockPos otherController = OxyUtil.isSealed(sLevel, current);
                    if (controller.equals(otherController)) {
                        OxyUtil.setBlockSealed(sLevel, current, null);
                        for (BlockPos neighbor : AbstractSealerBlockEntity.getAdjacent(current))
                            if (!VISITED.contains(neighbor)) QUEUE.add(neighbor);
                    }
                }

                OxyUtil.setBlockSealed(sLevel, controller, null);
            }
        }
    }

    public static void checkRefillBacktank(ServerPlayer sPlayer, boolean sealed, BlockPos controller) {
        List<ItemStack> tanks = OxyUtil.getAllBacktanks(sPlayer);
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