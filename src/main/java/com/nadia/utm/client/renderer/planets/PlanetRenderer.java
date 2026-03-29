package com.nadia.utm.client.renderer.planets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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

    public static class PLANETS {
        public static Planet EARTH = new Planet(150, 25, utm.key("textures/misc/earth.png"));
        public static Planet AG = new Planet(750, 10, utm.key("textures/misc/2313ag.png")) {
            @Override
            protected float getBrightness(long time, float partialTick) {
                return .85F;
            }

            @Override
            protected void transform(PoseStack poseStack) {
                poseStack.mulPose(Axis.XP.rotationDegrees(32));
                poseStack.mulPose(Axis.YP.rotationDegrees(12));
                poseStack.mulPose(Axis.ZP.rotationDegrees(5));
            }

            @Override
            protected boolean shouldPass(RenderLevelStageEvent event, Minecraft mc) {
                if (mc.level == null) return false;
                return mc.level.dimension().equals(Level.OVERWORLD);
            }
        };
    }

    @SubscribeEvent
    public static void onRenderSky(RenderLevelStageEvent event) {
        for (Planet planet : PLANET_REGISTRY)
            planet.onRenderSky(event);
    }

    public static void register() {
        utm.LOGGER.info("[UTM] Initialized planets, {}!", PLANETS.EARTH); // dont remove this or java never initializes the classes :DDD
    }
}
