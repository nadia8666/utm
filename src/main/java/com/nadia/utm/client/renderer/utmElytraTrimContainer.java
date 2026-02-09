package com.nadia.utm.client.renderer;

import com.nadia.utm.client.renderer.glint.StackComponentContainer;
import com.nadia.utm.registry.data.utmDataComponents;

public class utmElytraTrimContainer {
    public static final StackComponentContainer<Integer> TRIM_COLOR = new StackComponentContainer<>(
            utmDataComponents.ELYRA_TRIM_COLOR,
            0xFFFFFF
    );
    public static final StackComponentContainer<String> TRIM_TYPE = new StackComponentContainer<>(
            utmDataComponents.ELYRA_TRIM_TYPE,
            ""
    );
}
