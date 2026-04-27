package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.block.utmBlockContainer;
import com.nadia.utm.registry.item.utmItemContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class utmItemModelProvider extends ItemModelProvider {
    public utmItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, "utm", existingFileHelper);
    }

    @Override
    protected void registerModels() {
        utmBlockContainer.DATAGEN_TARGETS.forEach((c, tags) -> {
            for (String tag : tags) {
                if (tag.equals("blockModel"))
                    withExistingParent(c.ITEM.getId().toString(), modLoc("block/" + c.NAME));
            }
        });

        utmItemContainer.DATAGEN_TARGETS.forEach((c, tags) -> {
            for (String tag : tags) {
                if (tag.equals("generated"))
                    basicItem(c.ITEM().get());
                if (tag.equals("handheld"))
                    handheldItem(c.ITEM().get());
                if (tag.equals("disc"))
                    musicDisc(c.ITEM().get());
            }
        });
    }

    public void musicDisc(ResourceLocation item) {
        getBuilder(item.toString())
                .parent(new ModelFile.UncheckedModelFile("item/template_music_disc"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getNamespace(), "item/" + item.getPath()));
    }

    public void musicDisc(Item item) {
        musicDisc(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item)));
    }
}