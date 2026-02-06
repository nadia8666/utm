package com.nadia.utm.mixin.compat;

import com.nadia.utm.compat.GraveInterface;
import de.maxhenkel.gravestone.blocks.GraveStoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GraveStoneBlock.class, remap = false)
public class GraveBlockMixin {
    @Inject(
            method = "dropItems",
            at=@At("HEAD"),
            cancellable = true
    )
    public void utm$skipDrop(Level level, BlockPos pos, NonNullList<ItemStack> items, CallbackInfo ci) {
        var entity = level.getBlockEntity(pos);
        if (entity != null && ((GraveInterface) entity).utm$skipItems.get()) ci.cancel();
    }
}
