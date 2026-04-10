package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.util.AdvancementUtil;
import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.utm;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@ForceLoad
class Breathability {
    public static void checkSuffocating(ServerPlayer sPlayer, ServerLevel level, boolean inAG) {
        if (!OxyUtil.canBreathe(sPlayer) && level != null && !sPlayer.getAbilities().instabuild) {
            if (OxyUtil.isSealed(level, sPlayer.blockPosition()) != null) return;

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

                if (inAG) AdvancementUtil.AwardAdvancement(sPlayer, utm.key("2313ag/suffocate"));
            }
        }
    }
}