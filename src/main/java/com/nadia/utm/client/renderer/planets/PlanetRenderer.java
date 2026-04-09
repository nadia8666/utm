package com.nadia.utm.client.renderer.planets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nadia.utm.Config;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.nadia.utm.utm;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@ForceLoad(dist = Dist.CLIENT)
public class PlanetRenderer {
    public static final List<Planet> PLANET_REGISTRY = new ArrayList<>();

    // ordered by relative distance, moon renders over the earth which renders over the sun
    @ForceLoad(dist = Dist.CLIENT)
    public static class PLANETS {
        public static final Planet SUN = new Planet(300, 15, utm.key("textures/misc/sun.png")) {
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
        public static final Planet EARTH = new Planet(150, 25 / 3f, utm.key("textures/misc/earth.png")) {
            @Override
            public void transform(PoseStack poseStack, float partialTicks) {
                poseStack.mulPose(Axis.XP.rotationDegrees(11.2F));
            }
        };
        public static final Planet MOON = new Planet(150, 12.5f / 3f, utm.key("textures/misc/moon.png")) {
            @Override
            public float[] getColor(long time, float partialTicks) {
                float brightness = .25f + PLANETS.EARTH.getColor(time, partialTicks)[0] / 3;

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
        public static final Planet AG = new Planet(150, 3, utm.key("textures/misc/2313ag.png")) {
            @Override
            public float[] getColor(long time, float partialTicks) {
                double pTime = (time % 24000L) + partialTicks;
                double ang = (pTime / 24000.0) * 2.0 * Math.PI;
                float otherTime = (float) ((Math.sin(ang) + 1.0) / 2.0);
                float r = 0.7f - (otherTime * 0.4f);
                float g = 0.7f - (otherTime * 0.4f);
                float b = 0.7f + (otherTime * 0.3f);

                return new float[]{r, g, b};
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

    static {
        utmEvents.register(RenderLevelStageEvent.class, event -> {
            if (!Config.RENDER_PLANETS.getAsBoolean()) return;
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            long time = mc.level.dayTime() % 24000;

            for (Planet planet : PLANET_REGISTRY)
                planet.onRenderSky(time, mc, event);

            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        });
    }
}
