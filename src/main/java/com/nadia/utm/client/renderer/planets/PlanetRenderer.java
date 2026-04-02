package com.nadia.utm.client.renderer.planets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nadia.utm.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.nadia.utm.utm;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "utm", value = Dist.CLIENT)
public class PlanetRenderer {
    public static final List<Planet> PLANET_REGISTRY = new ArrayList<>();

    // ordered by relative distance, moon renders over the earth which renders over the sun
    public static class PLANETS {
        public static Planet SUN = new Planet(300, 15, utm.key("textures/misc/sun.png")) {
            @Override
            public float[] getColor(long time, float partialTicks) {
                return new float[]{1, 1, 1};
            }

            @Override
            public void transform(PoseStack poseStack, float partialTicks) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;
                long time = mc.level.dayTime();
                float offset = (((time + partialTicks) % 24000) / 24000) * 360 + 90;
                poseStack.mulPose(Axis.XP.rotationDegrees(11.2F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(offset));
            }
        };
        public static Planet EARTH = new Planet(150, 25/3f, utm.key("textures/misc/earth.png")) {
            @Override
            public void transform(PoseStack poseStack, float partialTicks) {
                poseStack.mulPose(Axis.XP.rotationDegrees(11.2F));
            }
        };
        public static Planet MOON = new Planet(150, 12.5f/3f, utm.key("textures/misc/moon.png")) {
            @Override
            public float[] getColor(long time, float partialTicks) {
                float brightness = .25f + PLANETS.EARTH.getColor(time, partialTicks)[0]/3;

                return new float[]{brightness, brightness, brightness};
            }

            @Override
            public void transform(PoseStack poseStack, float partialTicks) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;
                long time = mc.level.dayTime();

                float progress = ((time + partialTicks) % 24000) / 24000f;
                float angle = progress * (float) Math.PI * 2;
                float offset = (float) Math.cos(angle) * 60;
                float distance = (float) Math.sin(angle) * 60;

                poseStack.mulPose(Axis.XP.rotationDegrees(11.2F));
                poseStack.translate(offset, distance, 0);
            }
        };
        public static Planet AG = new Planet(150, 3, utm.key("textures/misc/2313ag.png")) {
            @Override
            public float[] getColor(long time, float partialTicks) {
                float brightness = 1 - PLANETS.EARTH.getColor(time, partialTicks)[0] / 2 - .175F;

                return new float[]{brightness / 1.25f, brightness / 1.25f, brightness * 1.25f};
            }

            @Override
            public float getAlpha(long time, float partialTick) {
                double pTime = (time % 24000L) + partialTick;
                double ang = (pTime / 24000.0) * 2.0 * Math.PI;
                return (float) (0.5 + (.5 * -Math.sin(ang)));
            }

            @Override
            public void transform(PoseStack poseStack, float partialTicks) {
                poseStack.mulPose(Axis.XP.rotationDegrees(32));
                poseStack.mulPose(Axis.ZP.rotationDegrees(12));
            }

            @Override
            public boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
                if (mc.level == null) return false;
                return mc.level.dimension().equals(Level.OVERWORLD);
            }
        };
    }

    @SubscribeEvent
    public static void onRenderSky(RenderLevelStageEvent event) {
        if (!Config.RENDER_PLANETS.getAsBoolean()) return;

        for (Planet planet : PLANET_REGISTRY)
            planet.onRenderSky(event);
    }

    public static void register() {
        utm.LOGGER.info("[UTM] Initialized planets, {}!", PLANETS.EARTH); // don't remove this or java never initializes the classes :DDD
    }
}
