package com.nadia.utm.datagen.providers;

import com.nadia.utm.registry.loot.AddItemModifier;
import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.registry.loot.ReplaceItemModifier;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class utmGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public utmGlobalLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, "utm");
    }

    @Override
    protected void start() {
        add("elytra_trim_spades_zombie", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                        LootItemRandomChanceCondition.randomChance(.005f).build()
                },
                utmItems.ELYTRA_TRIM_SPADES.get()
        ));

        add("elytra_trim_roadrunner_village", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/village/village_plains_house")).build(),
                        LootItemRandomChanceCondition.randomChance(.75f).build()
                },
                utmItems.ELYTRA_TRIM_ROADRUNNER.get()
        ));

        add("elytra_trim_ecologist_trail_ruins", new ReplaceItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                        LootItemRandomChanceCondition.randomChance(.05f).build()
                },
                utmItems.ELYTRA_TRIM_ECOLOGIST.get()
        ));

        add("elytra_trim_heartstwings_end_city", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/end_city_treasure")).build(),
                        LootItemRandomChanceCondition.randomChance(.20f).build()
                },
                utmItems.ELYTRA_TRIM_HEARTSTWINGS.get()
        ));

        add("elytra_trim_lesser_fortress", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/nether_bridge")).build(),
                        LootItemRandomChanceCondition.randomChance(.30f).build()
                },
                utmItems.ELYTRA_TRIM_LESSER.get()
        ));

        add("elytra_trim_outward_dungeon", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/simple_dungeon")).build(),
                        LootItemRandomChanceCondition.randomChance(.20f).build()
                },
                utmItems.ELYTRA_TRIM_OUTWARD.get()
        ));

        add("elytra_trim_vein_mansion", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("chests/woodland_mansion")).build(),
                        LootItemRandomChanceCondition.randomChance(.50f).build()
                },
                utmItems.ELYTRA_TRIM_VEIN.get()
        ));

        // this is completley untested i just fumbled my way trhough it
        add("music_disc_tears_ghast", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/ghast")).build(),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityType.FIREBALL)
                        ).build()
                },
                utmItems.MUSIC_DISC_TEARS.get()
        ));

        add("music_disc_lava_chicken_zombie", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/zombie")).build(),
                        LootItemRandomChanceCondition.randomChance(0.25f).build(),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity()
                                        .flags(net.minecraft.advancements.critereon.EntityFlagsPredicate.Builder.flags()
                                                .setIsBaby(true))
                        ).build()
                },
                utmItems.MUSIC_DISC_LAVA_CHICKEN.get()
        ));

        add("music_disc_undertale_creeper", new AddItemModifier(
                new LootItemCondition[]{
                        LootTableIdCondition.builder(ResourceLocation.withDefaultNamespace("entities/creeper")).build(),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.THIS,
                                EntityPredicate.Builder.entity()
                                        .nbt(new NbtPredicate(new CompoundTag() {{
                                            putBoolean("powered", true);
                                        }}))
                        ).build(),
                        LootItemEntityPropertyCondition.hasProperties(
                                LootContext.EntityTarget.DIRECT_ATTACKER,
                                EntityPredicate.Builder.entity().of(EntityType.SPECTRAL_ARROW)
                        ).build()
                },
                utmItems.MUSIC_DISC_UNDERTALE.get()
        ));
    }
}