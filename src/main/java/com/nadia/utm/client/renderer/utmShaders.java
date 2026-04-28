package com.nadia.utm.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.utm;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@ForceLoad(dist = Dist.CLIENT)
public class utmShaders {
    public static ShaderInstance GLINT_ADDITIVE;
    public static ShaderInstance GLINT_OVERLAY;
    public static ShaderInstance EMISSIVE_ARMOR_CUTOUT;

    static {
        utmEvents.register(RegisterShadersEvent.class, event -> {
            try {
                event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("utm", "glint/glint"),
                        DefaultVertexFormat.POSITION_TEX), (shader) -> GLINT_ADDITIVE = shader);

                event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("utm", "glint/glint_overlay"),
                        DefaultVertexFormat.POSITION_TEX), (shader) -> GLINT_OVERLAY = shader);

                event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath("utm", "emissive_armor_cutout"),
                        DefaultVertexFormat.NEW_ENTITY), (shader) -> EMISSIVE_ARMOR_CUTOUT = shader);
            } catch (IOException e) {
                utm.LOGGER.info("[UTM] Shader failure: {}", e.getMessage());
            }
        });
    }
}