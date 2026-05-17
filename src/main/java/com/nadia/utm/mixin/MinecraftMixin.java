package com.nadia.utm.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.nadia.utm.networking.payloads.MyAwesomeKarkParticlePayload;
import com.nadia.utm.networking.payloads.Sword2AttackPayload;
import com.nadia.utm.networking.payloads.jumbo_josh;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;


@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public HitResult hitResult;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Nullable
    public ClientLevel level;

    @Inject(
            method = "startAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/event/InputEvent$InteractionKeyMappingTriggered;isCanceled()Z",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void utm$postClick(CallbackInfoReturnable<Boolean> cir, @Local InputEvent.InteractionKeyMappingTriggered inputEvent, @Local ItemStack itemStack) {
        if (player == null) return;

        if (!inputEvent.isCanceled() && hitResult != null) {
            if (itemStack.is(utmTools.SWORD_OF_KIRK.get()) && player.getAttackStrengthScale(0f) >=1f && (hitResult.getType().equals(HitResult.Type.BLOCK) || hitResult.getType().equals(HitResult.Type.MISS))) {
                double xOff = -Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
                double yOff = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
                PacketDistributor.sendToServer(new MyAwesomeKarkParticlePayload(player.position().toVector3f(), (player.getLookAngle()).toVector3f(), xOff, yOff));

                if (inputEvent.shouldSwingHand())
                    player.swing(InteractionHand.MAIN_HAND);
                player.resetAttackStrengthTicker();

                cir.setReturnValue(false);
            } else if ((itemStack.is(utmItems.SWORD2.get()) || (itemStack.is(utmItems.GLOOMSWORD8.get()))) && player.getAttackStrengthScale(0f) >=1) {
                PacketDistributor.sendToServer(new Sword2AttackPayload(player.position().toVector3f()));
                if (inputEvent.shouldSwingHand())
                    player.swing(InteractionHand.MAIN_HAND);
                player.resetAttackStrengthTicker();


                //cir.setReturnValue(false);
            } else if (itemStack.is(utmItems.SABEL3.get()) && player.getAttackStrengthScale(0f ) >=1 && hitResult.getType().equals(HitResult.Type.ENTITY)) {
                if (hitResult instanceof EntityHitResult result) {
                    String targetUUID = result.getEntity().getUUID().toString();

                    PacketDistributor.sendToServer(new jumbo_josh(player.position().toVector3f(), targetUUID));
                    if (inputEvent.shouldSwingHand())
                        player.swing(InteractionHand.MAIN_HAND);
                    player.resetAttackStrengthTicker();

                    // for some reason on these special conditions it doesn't actually hit the mob.. which is somethign that is NOt good. i think removing this--
                    //fixes that.
                    //  cir.setReturnValue(false);
                }
            }
        }
    }
}
