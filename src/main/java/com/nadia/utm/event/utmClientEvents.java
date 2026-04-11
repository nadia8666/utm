package com.nadia.utm.event;

import com.nadia.utm.client.renderer.BacktankCurioRenderer;
import com.nadia.utm.client.renderer.block.CitywallsBlockEntityRenderer;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.client.ui.glint.GlintScreen;
import com.nadia.utm.client.ui.oxygen_furnace.OxygenFurnaceScreen;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.registry.ui.utmMenus;
import com.nadia.utm.updater.ToastDisplaySignal;
import com.nadia.utm.utm;
import com.nadia.utm.utmClient;
import com.simibubi.create.AllItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import static com.nadia.utm.updater.AutoUpdater.ToastReady;

@ForceLoad(dist = Dist.CLIENT)
public class utmClientEvents {
    @ForceLoad(dist = Dist.CLIENT)
    static class UI {
        static {
            utmEvents.register(ScreenEvent.Init.Post.class, event -> {
                if (!ToastReady && event.getScreen() instanceof TitleScreen) {
                    ToastReady = true;
                    utmClient.tryToastPopup();
                }
            });

            utmEvents.register(RegisterMenuScreensEvent.class, event -> {
                event.register(utmMenus.GLINT_MENU.get(), GlintScreen::new);
                event.register(utmMenus.OXYGEN_FURNACE_MENU.get(), OxygenFurnaceScreen::new);
            });

            utmEvents.register(ItemTooltipEvent.class, event -> {
                ItemStack stack = event.getItemStack();
                var type = stack.getOrDefault(utmDataComponents.ELYRA_TRIM_TYPE.get(), "");
                int color = stack.getOrDefault(utmDataComponents.ELYRA_TRIM_COLOR.get(), 0xFFFFFF);

                if (!type.isEmpty()) {
                    var material = switch (color) {
                        case 0xABABAB -> "Iron";
                        case 0xFFBE3D -> "Copper";
                        case 0xFFE924 -> "Gold";
                        case 0x2F24FF -> "Lapis Lazuli";
                        case 0x24FF2B -> "Emerald";
                        case 0x24F0FF -> "Diamond";
                        case 0x121212 -> "Netherite";
                        case 0xEB1515 -> "Redstone";
                        case 0xFFFFFF -> "Quartz";
                        case 0xB116E0 -> "Amethyst";
                        default -> "Unknown";
                    };

                    event.getToolTip().add(1, Component.literal(" "));

                    event.getToolTip().add(1, Component.literal(" " + material + " Material")
                            .withStyle(style -> style.withColor(TextColor.fromRgb(color))));

                    event.getToolTip().add(1, Component.translatable("utm.elytra_trim_type." + type)
                            .withStyle(style -> style.withColor(TextColor.fromRgb(color))));

                    event.getToolTip().add(1, Component.literal("Upgrade:")
                            .withStyle(ChatFormatting.GRAY));
                }
            });

            utmEvents.register(ToastDisplaySignal.class, event -> utmClient.tryToastPopup());
        }
    }

    @ForceLoad(dist = Dist.CLIENT)
    static class RENDERER {
        static {
            utmEvents.register(RegisterRenderBuffersEvent.class, event -> {
                event.registerRenderBuffer(utmRenderTypes.ADDITIVE_GLINT_ITEM.get());
                event.registerRenderBuffer(utmRenderTypes.OVERLAY_GLINT_ITEM.get());
                event.registerRenderBuffer(utmRenderTypes.ADDITIVE_GLINT_ENTITY.get());
                event.registerRenderBuffer(utmRenderTypes.OVERLAY_GLINT_ENTITY.get());
            });

            utmEvents.register(FMLClientSetupEvent.class, event -> {
                CuriosRendererRegistry.register(AllItems.COPPER_BACKTANK.get(), BacktankCurioRenderer::new);
                CuriosRendererRegistry.register(AllItems.NETHERITE_BACKTANK.get(), BacktankCurioRenderer::new);
            });

            utmEvents.register(ModelEvent.RegisterAdditional.class, event -> {
                event.register(CitywallsBlockEntityRenderer.CWL);
                event.register(CitywallsBlockEntityRenderer.OWM);
                event.register(CitywallsBlockEntityRenderer.OWS);
                event.register(CitywallsBlockEntityRenderer.CWS);
            });

            utmEvents.register(RegisterDimensionSpecialEffectsEvent.class, event -> event.register(
                    ResourceLocation.fromNamespaceAndPath("utm", "2313ag"),
                    new DimensionSpecialEffects(Float.NaN, false, DimensionSpecialEffects.SkyType.NONE, false, false) {
                @Override
                public @NotNull Vec3 getBrightnessDependentFogColor(@NotNull Vec3 pos, float arg2) {
                    return Vec3.ZERO;

                }

                @Override
                public boolean isFoggyAt(int x, int z) {
                    return true;
                }

                @Override
                public float[] getSunriseColor(float timeOfDay, float partialTicks) {
                    return null;
                }
            }));
        }
    }

    static {
        utmEvents.register(RegisterClientExtensionsEvent.class, event -> {
            event.registerFluidType(new IClientFluidTypeExtensions() {
                private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
                private static final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return STILL;
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return FLOWING;
                }

                @Override
                public int getTintColor() {
                    return 0xFFB3E5FC;
                }
            }, utmFluids.LIQUID_OXYGEN_TYPE);

            event.registerFluidType(new IClientFluidTypeExtensions() {
                private static final ResourceLocation STILL = utm.key("block/molten_steel_still");
                private static final ResourceLocation FLOWING = utm.key("block/molten_steel_flow");

                @Override
                public @NotNull ResourceLocation getStillTexture() {
                    return STILL;
                }

                @Override
                public @NotNull ResourceLocation getFlowingTexture() {
                    return FLOWING;
                }
            }, utmFluids.MOLTEN_STEEL_TYPE);
        });

        utmEvents.register(PlaySoundEvent.class, event -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || event.getSound() == null) return;

            if (mc.level.dimension().equals(utmDimensions.AG_KEY) && event.getSound().getSource() == SoundSource.MUSIC && !event.getSound().getLocation().getNamespace().equals("utm"))
                event.setSound(null);
        });
    }
}
