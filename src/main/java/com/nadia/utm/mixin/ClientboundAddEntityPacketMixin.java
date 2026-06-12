package com.nadia.utm.mixin;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ClientboundAddEntityPacket.class, remap = false)
public class ClientboundAddEntityPacketMixin {
    @Shadow
    private byte xRot;
    @Shadow
    private byte yRot;
    @Unique
    private byte utm$xRot2, utm$yRot2;

    @Unique
    private short utm$createEncodedY(float in) {
        return (short) (in / 360 * 65535);
    }

    @Unique
    private short utm$packX() {
        return (short) ((xRot & 255) << 8 | (utm$xRot2 & 255));
    }

    @Unique
    private short utm$packY() {
        return (short) ((yRot & 255) << 8 | (utm$yRot2 & 255));
    }

    @Unique
    private short utm$createEncodedX(float in) {
        return (short) (in / 180 * 65535);
    }

    @Inject(method = "<init>(ILjava/util/UUID;DDDFFLnet/minecraft/world/entity/EntityType;ILnet/minecraft/world/phys/Vec3;D)V", at = @At("TAIL"))
    private void add2Byte(int pId, UUID pUuid, double pX, double pY, double pZ, float pXRot, float pYRot, EntityType<?> pType, int pData, Vec3 pDeltaMovement, double pYHeadRot, CallbackInfo ci) {
        short xRotData = utm$createEncodedX(pXRot);
        xRot = (byte) (xRotData >> 8);
        utm$xRot2 = (byte) (xRotData & 255);
        short yRotData = utm$createEncodedY(pYRot);
        yRot = (byte) (yRotData >> 8);
        utm$yRot2 = (byte) (yRotData & 255);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void addExtraByteToNW(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeByte(utm$xRot2);
        buffer.writeByte(utm$yRot2);
    }

    @Inject(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At("TAIL"))
    private void readExtraByteFromNW(RegistryFriendlyByteBuf buffer, CallbackInfo ci) {
        this.utm$xRot2 = buffer.readByte();
        this.utm$yRot2 = buffer.readByte();
    }

    /**
     * @author Kapitencraft
     * @reason precision fix
     */
    @Overwrite
    public float getYRot() {
        return ((float) utm$packY()) / 65535 * 360;
    }

    /**
     * @author Kapitencraft
     * @reason precision fix
     */
    @Overwrite
    public float getXRot() {
        return ((float) utm$packX()) / 65535 * 180;
    }
}
