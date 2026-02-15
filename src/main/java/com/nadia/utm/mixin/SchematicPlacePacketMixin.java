package com.nadia.utm.mixin;

import com.nadia.utm.registry.data.utmDataComponents;
import com.simibubi.create.content.schematics.packet.SchematicPlacePacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SchematicPlacePacket.class, remap = false)
public class SchematicPlacePacketMixin {
    @Unique
    private boolean utm$getValid(ServerPlayer player) {
        return player.isCreative() || player.getMainHandItem().has(utmDataComponents.IS_PLACABLE_SCHEMATIC.get()) || player.getOffhandItem().has(utmDataComponents.IS_PLACABLE_SCHEMATIC.get());
    }

    @Inject(method = "handle", at = @At("TAIL"))
    private void utm$consumeItem(ServerPlayer player, CallbackInfo ci) {
        var mainHand = player.getMainHandItem();
        var offHand = player.getOffhandItem();

        if (mainHand.has(utmDataComponents.IS_PLACABLE_SCHEMATIC.get())) {
            mainHand.setCount(0);
            player.containerMenu.broadcastChanges();
        }

        if (offHand.has(utmDataComponents.IS_PLACABLE_SCHEMATIC.get())) {
            offHand.setCount(0);
            player.containerMenu.broadcastChanges();
        }
    }

    @Redirect(method = "handle", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isCreative()Z"))
    private boolean utm$validate(ServerPlayer player) {
        return utm$getValid(player);
    }
}