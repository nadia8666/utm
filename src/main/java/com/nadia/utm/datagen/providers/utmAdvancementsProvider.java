package com.nadia.utm.datagen.providers;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class utmAdvancementsProvider extends net.neoforged.neoforge.common.data.AdvancementProvider {
    public utmAdvancementsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(
                new DiscAdvancementGenerator()
        ));
    }

    private static final class DiscAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
        @Override
        public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> saver, @NotNull ExistingFileHelper existingFileHelper) {
            Advancement.Builder builder = Advancement.Builder.advancement();
            builder.parent(AdvancementSubProvider.createPlaceholder("minecraft:adventure/play_jukebox_in_meadows"));
            builder.display(
                    Items.MUSIC_DISC_STAL,
                    Component.translatable("advancements.utm.all_discs.title"),
                    Component.translatable("advancements.utm.all_discs.description"),
                    null,
                    AdvancementType.GOAL,
                    true,
                    true,
                    false
            );

            var pickups = new HashMap<String, Criterion<?>>();
            var list = registries.lookupOrThrow(Registries.ITEM);
            list.listElements().filter(item -> item.value().components().has(DataComponents.JUKEBOX_PLAYABLE)).forEach(item -> {
                var key = "has_" + item.getRegisteredName();
                var criteria = InventoryChangeTrigger.TriggerInstance.hasItems(item.value());

                pickups.put(key, criteria);
                builder.addCriterion(key, criteria);
            });

            builder.rewards(
                    AdvancementRewards.Builder.experience(100)
            );

            builder.requirements(AdvancementRequirements.allOf(pickups.keySet()));
            builder.save(saver, ResourceLocation.fromNamespaceAndPath("utm", "adventure/all_discs"), existingFileHelper);
        }
    }
}
