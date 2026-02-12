package com.nadia.utm.mixin;

import com.nadia.utm.client.renderer.ElytraUtil;
import com.nadia.utm.item.NetherytraItem;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.utm;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, remap = false)
public class GameRendererMixin {
    @Unique
    private float utm$trailAccumulator = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void utm$renderTrail(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (renderLevel && mc.level != null && mc.player != null && mc.options.getCameraType().isFirstPerson() && mc.player.isFallFlying()) {
            utm$trailAccumulator += deltaTracker.getRealtimeDeltaTicks() * 3;

            while (utm$trailAccumulator > 1) {
                LocalPlayer player = mc.player;
                ItemStack itemstack = player.getItemBySlot(EquipmentSlot.CHEST);

                Integer color = itemstack.get(utmDataComponents.ELYRA_TRIM_COLOR.get());
                String type = itemstack.get(utmDataComponents.ELYRA_TRIM_TYPE.get());

                if (color != null && type != null) {
                    float r = ((color >> 16) & 0xFF) / 255f;
                    float g = ((color >> 8) & 0xFF) / 255f;
                    float b = (color & 0xFF) / 255f;

                    var camera = mc.gameRenderer.getMainCamera();
                    var pos = camera.getPosition();
                    var forward = new Vec3(camera.getLookVector()).scale(1);
                    var rightUnit = new Vec3(camera.getLeftVector()).scale(-1.0);

                    var backwardOffset = forward.scale(2.0);
                    var leftVec = pos.subtract(rightUnit).subtract(backwardOffset).scale(1);
                    var rightVec = pos.add(rightUnit).subtract(backwardOffset).scale(1);
                    utm.LOGGER.warn(type);
                    ElytraUtil.spawnTrail(
                            mc.level, type, r, g, b,
                            leftVec.x, leftVec.y, leftVec.z,
                            rightVec.x, rightVec.y, rightVec.z
                    );
                }
                if (itemstack.getItem() instanceof NetherytraItem netherytraItem) {
                    float r = 1f;
                    float g = 1f;
                    float b = 1f;

                    var camera = mc.gameRenderer.getMainCamera();
                    var pos = camera.getPosition();
                    var forward = new Vec3(camera.getLookVector()).scale(1);
                    var rightUnit = new Vec3(camera.getLeftVector()).scale(-1);

                    var backwardOffset = forward.scale(2.0);
                    var leftVec = pos.subtract(rightUnit).scale(0.66).subtract(backwardOffset);
                    var rightVec = pos.add(rightUnit).scale(0.66).subtract(backwardOffset);

                    ElytraUtil.spawnTrail(
                            mc.level, "nep", r, g, b,
                            leftVec.x, leftVec.y, leftVec.z,
                            rightVec.x, rightVec.y, rightVec.z
                    );
                     leftVec = pos.subtract(rightUnit).scale(0.33).subtract(backwardOffset);
                     rightVec = pos.add(rightUnit).scale(0.33).subtract(backwardOffset);
                    ElytraUtil.spawnTrail(
                            mc.level, "nep", r, g, b,
                            leftVec.x, leftVec.y, leftVec.z,
                            rightVec.x, rightVec.y, rightVec.z
                    );
                }

                utm$trailAccumulator--;
            }
        } else if (utm$trailAccumulator > 0) utm$trailAccumulator = 0;
    }
}