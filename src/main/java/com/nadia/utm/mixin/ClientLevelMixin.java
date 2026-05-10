package com.nadia.utm.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientLevel.class, remap = false)
public class ClientLevelMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void utmgetSky(Vec3 pos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        float alpha = utm$alpha(this.minecraft.gameRenderer.getMainCamera().getPosition().y);
        if (alpha > 0) {
            Vec3 original = cir.getReturnValue();
            cir.setReturnValue(original.scale(1.0F - alpha));
        }
    }

    // the other versions are bad.
    @Inject(method = "getStarBrightness", at = @At("RETURN"), cancellable = true)
    private void utm$twinkleTwinkleLittleStarHowIWonderWhatYouAreUpAboveTheWorldSoHighLikeADiamondInTheSkyWhenTheBlazingSunIsGoneWhenHeNothingShinesUponThenYouShowYourLittleLightTwinkleTwinkleAllTheNightThenTheTravelerInTheDarkThanksYouForYourTinySparkHeCouldNotSeeWhichWayToGoIfYouDidNotTwinkleSoInTheDarkBlueSkyYouKeepAndOftenThroughMyCurtainsPeepForYouNeverShutYourEyeTillTheSunIsInTheSkyItsYourBrightAndTinySparkLightsTheTravelerInTheDarkThoughIKnowNotWhatYouAreTwinkleTwinkleLittleStar(float partialTick, CallbackInfoReturnable<Float> cir) {
        float alpha = utm$alpha(this.minecraft.gameRenderer.getMainCamera().getPosition().y);
        if (alpha > 0) {
            float intendedBrightness = cir.getReturnValue();
            cir.setReturnValue(Mth.lerp(alpha, intendedBrightness, 0.5F));
        }
    }

    @Inject(method = "getCloudColor", at = @At("RETURN"), cancellable = true)
    private void utm$greatResponsibility(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        float alpha = utm$alpha(this.minecraft.gameRenderer.getMainCamera().getPosition().y);
        if (alpha > 0) {
            cir.setReturnValue(cir.getReturnValue().scale(1.0F - alpha));
        }
    }

    @Unique
    private static float utm$alpha(double y) {
        return (float) Math.clamp((y - 5000.0) / 1000.0, 0.0, 1.0);
    }
}
