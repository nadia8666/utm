package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlockContainer;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class utmBlockModelProvider extends BlockStateProvider {
    public utmBlockModelProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, "utm", helper);
    }

    @Override
    protected void registerStatesAndModels() {
        utmBlockContainer.DATAGEN_TAGS.forEach((container, tags) -> {
            Block block = container.BLOCK.get();

            if (tags.contains("blockModelState"))
                simpleBlock(block);
        });
    }
}
