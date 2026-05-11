package com.nadia.utm.client.renderer.planets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.nadia.utm.config.utmClientConfig;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.planets.utmPlanets;
import com.nadia.utm.utm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@ForceLoad(dist = Dist.CLIENT)
public class PlanetRenderer {
    public static final List<RenderedPlanet> RENDERED_PLANET_REGISTRY = new ArrayList<>();

    // ordered by relative distance, moon renders over the earth which renders over the sun
    @ForceLoad(dist = Dist.CLIENT)
    public static class PLANETS {
        public static final RenderedPlanet SUN_AG23 = new RenderedPlanet(utm.key("textures/misc/sun.png")) {
            @Override
            public float getDistance() {
                return 300;
            }

            @Override
            public float getSize() {
                return 15;
            }

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

            @Override
            public boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
                return mc.level != null && utmPlanets.AG23.is(utmPlanets.get(mc.level));
            }
        };
        public static final RenderedPlanet EARTH_AG23 = new RenderedPlanet(utm.key("textures/misc/earth.png")) {
            @Override
            public float getDistance() {
                return 150;
            }

            @Override
            public float getSize() {
                return 8.333333f;
            }

            @Override
            public void transform(PoseStack poseStack, float partialTicks) {
                poseStack.mulPose(Axis.XP.rotationDegrees(11.2F));
            }

            @Override
            public boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
                return mc.level != null && utmPlanets.AG23.is(utmPlanets.get(mc.level));
            }
        };
        public static final RenderedPlanet MOON_AG23 = new RenderedPlanet(utm.key("textures/misc/moon.png")) {
            @Override
            public float getDistance() {
                return 150;
            }

            @Override
            public float getSize() {
                return 4.166666666666667F;
            }

            @Override
            public float[] getColor(long time, float partialTicks) {
                float brightness = .25f + PLANETS.EARTH_AG23.getColor(time, partialTicks)[0] / 3;

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

            @Override
            public boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
                return mc.level != null && utmPlanets.AG23.is(utmPlanets.get(mc.level));
            }
        };

        public static final RenderedPlanet AG23_EARTH = new RenderedPlanet(utm.key("textures/misc/2313ag.png")) {
            @Override
            public float getDistance() {
                return 150;
            }

            @Override
            public float getSize() {
                return 3;
            }

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
                return mc.level != null && utmPlanets.EARTH.is(utmPlanets.get(mc.level));
            }
        };
        public static final RenderedPlanet EARTH_EARTH = new RenderedPlanet(utm.key("textures/misc/earth.png")) {
            @Override
            public float getDistance() {
                return 10;
            }

            @Override
            public float getSize() {
                return 450;
            }

            @Override
            public void transform(PoseStack poseStack, float partialTicks) {
                poseStack.translate(0, -50, 0);
                poseStack.mulPose(Axis.XP.rotationDegrees(180));

                Minecraft mc = Minecraft.getInstance();
                if (mc.player instanceof LocalPlayer player) {
                    float scalar = Math.max((float) (1 - (player.getY() / 125_000)), 0);
                    poseStack.scale(scalar, 1, scalar);
                }
            }

            @Override
            public boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
                return mc.level != null && utmPlanets.EARTH.is(utmPlanets.get(mc.level));
            }

            @Override
            public float getAlpha(long time, float partialTick) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player instanceof LocalPlayer player) {
                    double y = player.position().y;

                    if (y > 5000) {
                        return Math.min((float) ((y - 5000) / 1000), 1);
                    } else
                        return 0;
                }

                return 0;
            }
        };

        public static final RenderedPhysicalPlanet SUN_SPACE = new RenderedPhysicalPlanet(
                utm.key("textures/misc/sun.png"), utmPlanets.SUN.ORBIT(), 2180.0f
        ) {
            @Override
            public float[] getColor(long time, float partialTicks) {
                return new float[]{1, 1, 1};
            }
        };

        public static final RenderedPhysicalPlanet EARTH_SPACE = new RenderedPhysicalPlanet(
                utm.key("textures/misc/earth.png"),
                utmPlanets.EARTH.ORBIT(),
                80.0f
        );

        public static final RenderedPhysicalPlanet MOON_SPACE = new RenderedPhysicalPlanet(
                utm.key("textures/misc/moon.png"),
                utmPlanets.MOON.ORBIT(),
                20.0f
        );

        public static final RenderedPhysicalPlanet AG23_SPACE = new RenderedPhysicalPlanet(
                utm.key("textures/misc/2313ag.png"),
                utmPlanets.AG23.ORBIT(),
                30.0f
        );
    }

    static {
        utmEvents.register(RenderLevelStageEvent.class, event -> {
            if (!utmClientConfig.RENDER_PLANETS.getAsBoolean()) return;
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            long time = mc.level.dayTime();

            for (RenderedPlanet planet : RENDERED_PLANET_REGISTRY)
                planet.onRenderSky(time, mc, event);

            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
        });
    }
}
