package com.nadia.utm.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.nadia.utm.networking.payloads.FUCKPayload;
import com.nadia.utm.registry.item.tool.utmTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
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
        if (!inputEvent.isCanceled() && hitResult != null && (hitResult.getType().equals(HitResult.Type.BLOCK) || hitResult.getType().equals(HitResult.Type.MISS))) {
            if (itemStack.is(utmTools.SWORD_OF_KIRK.get())) {
                if (inputEvent.shouldSwingHand())
                    player.swing(InteractionHand.MAIN_HAND);
                    //joe miden?

                double d0 = (double) (-Mth.sin(player.getYRot() * ((float)Math.PI / 180F)));
                double d1 = (double) Mth.cos(player.getYRot() * ((float)Math.PI / 180F));
                Vector3f pos = new Vector3f((float) player.getX(), (float) player.getY((double)0.5F), (float) player.getZ());
                PacketDistributor.sendToServer(new FUCKPayload(pos, (player.getLookAngle()).toVector3f(),d0,d1));
                if (player.level() instanceof ServerLevel) {

                    ((ServerLevel)player.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + d0, player.getY((double)0.5F), player.getZ() + d1, 0, d0, (double)0.0F, d1, (double)0.0F);
                }
                cir.setReturnValue(false);
            }
        }
    }
}
