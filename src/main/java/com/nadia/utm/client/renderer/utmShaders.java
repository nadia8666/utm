package com.nadia.utm.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.nadia.utm.utm;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = "utm", value = Dist.CLIENT)
public class utmShaders {
    public static ShaderInstance GLINT_ADDITIVE;
    public static ShaderInstance GLINT_OVERLAY;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("utm", "glint/glint"),
                    DefaultVertexFormat.POSITION_TEX), (shader) -> GLINT_ADDITIVE = shader);

            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    ResourceLocation.fromNamespaceAndPath("utm", "glint/glint_overlay"),
                    DefaultVertexFormat.POSITION_TEX), (shader) -> GLINT_OVERLAY = shader);
        } catch (IOException e) {
            utm.LOGGER.info("[UTM] Shader failure: {}", e.getMessage());
        }
    }
}