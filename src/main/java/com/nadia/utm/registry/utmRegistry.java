package com.nadia.utm.registry;

import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.registry.behavior.utmMovingInteractions;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import com.nadia.utm.registry.loot.utmLoot;
import com.nadia.utm.registry.particle.utmParticles;
import com.nadia.utm.registry.recipe.utmRecipes;
import com.nadia.utm.registry.sound.utmSounds;
import com.nadia.utm.registry.ui.utmMenus;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.nadia.utm.registry.buffs.utmBuffs;

public class utmRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "utm");

    public static final DeferredRegister.DataComponents COMPONENTS = utmDataComponents.COMPONENTS;
    public static final DeferredRegister.Items ITEMS = utmItems.ITEMS;
    public static final DeferredRegister.Blocks BLOCKS = utmBlocks.BLOCKS;
    public static final DeferredRegister<?> MENUS = utmMenus.MENUS;
    public static final DeferredRegister<?> BLOCK_ENTITIES = utmBlockEntities.BLOCK_ENTITIES;
    public static final DeferredRegister<?> SOUNDS = utmSounds.SOUNDS;
    public static final DeferredRegister<?> BUFFS = utmBuffs.BUFFS;
    public static final DeferredRegister<?> RECIPE_SERIALIZERS = utmRecipes.RECIPE_SERIALIZERS;
    public static final DeferredRegister<?> PARTICLE_TYPES = utmParticles.PARTICLE_TYPES;
    public static final DeferredRegister<?> GLOBAL_LOOT_MODIFIER_SERIALIZERS = utmLoot.GLOBAL_LOOT_MODIFIER_SERIALIZERS;
    public static final DeferredRegister<?> ATTACHMENTS = utmAttachments.ATTACHMENTS;

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS
            .register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.utm"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(utmItems.MUSIC_DISC_UNDERTALE.get()::getDefaultInstance)
                    .displayItems((parameters, output) -> {
                        utmRegistry.BLOCKS.getEntries().forEach(entry -> output.accept(entry.get()));
                        utmRegistry.ITEMS.getEntries().forEach(entry -> output.accept(entry.get()));
                    }).build());

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(utmBlocks.HEAVY_METAL_ANVIL.item);
        }
    }

    public static void registerAll(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        COMPONENTS.register(modEventBus);
        MENUS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        SOUNDS.register(modEventBus);
        BUFFS.register(modEventBus);
        utmTools.doNothing();
        TABS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
        GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);
        ATTACHMENTS.register(modEventBus);
    }

    public static void lateRegister() {
        utmMovingInteractions.register();
    }
}
