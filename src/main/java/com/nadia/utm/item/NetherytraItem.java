package com.nadia.utm.item;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class NetherytraItem extends ElytraItem {
    public NetherytraItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.NETHERITE_INGOT);
    }
    @Override
    public @NotNull Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    public double createRandom() {
        return (Math.random()-0.5)*2;
    };

    @Override
    public boolean elytraFlightTick(@NotNull ItemStack stack, LivingEntity entity, int flightTicks) {
        var pos = entity.position();
        if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
                var lA = (entity.getDeltaMovement().length())*8;
                var lA2 = (int) Math.max(0,Math.floor(lA))-5;

                if (lA2 >1) {
                    level.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, lA2 * 2, 1 + (double) lA2 /3, 1 + (double) lA2 /3, 1 + (double) lA2 /3, 0);
                }
            }


            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                }

                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }

        return true;
    }
}
