package com.nadia.utm.mixin;

import com.nadia.utm.registry.dimension.utmDimensions;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.armor.RemainingAirOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = RemainingAirOverlay.class, remap = false)
public class RemainingAirOverlayMixin {
    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isInLava()Z")
    )
    private boolean utm$2313AOxygen(LocalPlayer instance) {
        if (instance.level().dimension().equals(utmDimensions.AG_KEY))
            return true;

        return instance.isInLava();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;contains(Ljava/lang/String;)Z"))
    private boolean utm$setOxy(CompoundTag instance, String key) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.level().dimension().equals(utmDimensions.AG_KEY)) {
            List<ItemStack> tanks = BacktankUtil.getAllWithAir(mc.player);
            int fill = 0;
            for (ItemStack stack : tanks)
                fill += BacktankUtil.getAir(stack);

            if (fill > 0) {
                instance.putInt("VisualBacktankAir", fill);
                return true;
            }
        }

        return instance.contains(key);
    }
}
