package com.nadia.utm.block.displaylink;

import com.nadia.utm.registry.block.utmBlockContainer;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.registry.CreateRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class utmDisplaySources {
    public static final DeferredRegister<DisplaySource> DISPLAY_SOURCES = DeferredRegister.create(CreateRegistries.DISPLAY_SOURCE, "utm");
    public static final Map<utmBlockContainer<?, ?>, Supplier<List<DisplaySource>>> ALL_SOURCES_BLOCK = new HashMap<>();
    public static final Map<utmBlockContainer<?, ?>, Supplier<List<DisplaySource>>> ALL_SOURCES_ENTITY = new HashMap<>();

    public static final DeferredHolder<DisplaySource, SealerDisplaySource> SEALER = DISPLAY_SOURCES.register("sealer_display_source", SealerDisplaySource::new);

    public static void registerSources() {
        for (Map.Entry<utmBlockContainer<?, ?>, Supplier<List<DisplaySource>>> source : ALL_SOURCES_BLOCK.entrySet()) {
            List<DisplaySource> sources = source.getValue().get();
            utmBlockContainer<?, ?> block = source.getKey();

            DisplaySource.BY_BLOCK.register(block.BLOCK.get(), sources);
        }

        for (Map.Entry<utmBlockContainer<?, ?>, Supplier<List<DisplaySource>>> source : ALL_SOURCES_BLOCK.entrySet()) {
            List<DisplaySource> sources = source.getValue().get();
            utmBlockContainer<?, ?> block = source.getKey();

            var type = utmBlockEntities.BLOCK_BINDINGS.get(block);
            if (type != null)
                DisplaySource.BY_BLOCK_ENTITY.register(type, sources);
        }
    }
}
