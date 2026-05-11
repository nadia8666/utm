package com.nadia.utm.mixin;

import com.nadia.utm.config.utmServerConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ElytraItem.class, remap = false)
public class ElytraItemMixin {
    /**
     * @author nadiarr
     * @reason because i can
     */
    @SuppressWarnings("SameReturnValue")
    @Overwrite
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;

            if (nextFlightTick % utmServerConfig.ELYTRA_DECAY_TIME.getAsInt() == 0)
                stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);

            if (nextFlightTick % 10 == 0)
                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
        }
        return true;
    }
}
