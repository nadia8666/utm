package com.nadia.utm.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.nadia.utm.networking.MathHelper;
import com.nadia.utm.networking.ScaledClientMotionPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientboundSetEntityMotionPacket.class, remap = false)
public class ClientboundSetEntityMotionPacketMixin implements ScaledClientMotionPacket {
    @Shadow
    public int xa;
    @Shadow
    public int ya;
    @Shadow
    public int za;

    @Unique
    private float utm$deltaScale = 1.0f;

    @Inject(method = "<init>(ILnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
    private void calculateScale(int pId, Vec3 pDeltaMovement, CallbackInfo ci, @Local(ordinal = 1) double d1, @Local(ordinal = 2) double d2, @Local(ordinal = 3) double d3) {
        float scale = MathHelper.getOversizeScale(pDeltaMovement, new Vec3(d1, d2, d3));
        this.utm$deltaScale = scale > 0.0f ? scale : 1.0f;

        this.xa = (int) (pDeltaMovement.x * utm$deltaScale * 8000);
        this.ya = (int) (pDeltaMovement.y * utm$deltaScale * 8000);
        this.za = (int) (pDeltaMovement.z * utm$deltaScale * 8000);
    }

    @ModifyConstant(method = {"getXa", "getYa", "getZa"}, constant = @Constant(doubleValue = 8000d))
    private double addDeltaScale(double constant) {
        if (this.utm$deltaScale <= 0.0f)
            return constant;
        return constant / this.utm$deltaScale;
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
    private void readScaleFromNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        if (pBuffer.isReadable(4)) {
            float scale = pBuffer.readFloat();
            this.utm$deltaScale = scale > 0.0f ? scale : 1.0f;
        } else {
            this.utm$deltaScale = 1.0f;
        }
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void addScaleToNW(FriendlyByteBuf pBuffer, CallbackInfo ci) {
        pBuffer.writeFloat(this.utm$deltaScale);
    }

    @Override
    public float utm$getScale() {
        return this.utm$deltaScale > 0.0f ? this.utm$deltaScale : 1.0f;
    }
}