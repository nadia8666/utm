package com.nadia.utm.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = Camera.class, remap = false)
public class CameraMixin {

    @Final
    @Shadow @Mutable
    private Vector3f horizontalPlane;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void ean_test(CallbackInfo ci){
        this.horizontalPlane = new Vector3f(0.0F, 2.0F, 0.0F);
    }
}
