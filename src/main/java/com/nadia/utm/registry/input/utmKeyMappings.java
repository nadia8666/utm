package com.nadia.utm.registry.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@ForceLoad(dist= Dist.CLIENT)
public class utmKeyMappings {
    public static final KeyMapping ADVANCED_GOGGLES_PIN_KEY = new KeyMapping(
            "key.utm.pin_panel",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_SEMICOLON,
            "key.categories.utm"
    );

    static {
        utmEvents.register(RegisterKeyMappingsEvent.class, event -> event.register(ADVANCED_GOGGLES_PIN_KEY));
    }
}
