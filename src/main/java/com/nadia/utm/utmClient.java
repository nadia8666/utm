package com.nadia.utm;

import com.nadia.utm.block.GrateBlock;
import com.nadia.utm.client.renderer.BacktankCurioRenderer;
import com.nadia.utm.client.renderer.CitywallsBlockEntityRenderer;
import com.nadia.utm.client.renderer.OxygenCollectorRenderer;
import com.nadia.utm.client.renderer.glint.utmGlintContainer;
import com.nadia.utm.client.renderer.planets.PlanetRenderer;
import com.nadia.utm.client.ui.glint.GlintScreen;
import com.nadia.utm.client.updater.UpdateToast;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.data.utmDataComponents;
import com.nadia.utm.client.ponder.utmPonderPlugin;
import com.nadia.utm.registry.fluid.utmFluids;
import com.nadia.utm.registry.model.utmPartialModels;
import com.nadia.utm.registry.ui.utmMenus;
import com.nadia.utm.client.renderer.utmRenderTypes;
import com.nadia.utm.updater.ToastDisplaySignal;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.wrench.RadialWrenchMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.nadia.utm.updater.AutoUpdater.*;

@Mod(value = utm.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = utm.MODID, value = Dist.CLIENT)
public class utmClient {
    public utmClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        RadialWrenchMenu.registerRotationProperty(GrateBlock.VERTICAL_DIRECTION, "Vertical Direction");
        utmPartialModels.register();
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> Minecraft.getInstance().getTextureManager().getTexture(utmGlintContainer.GLINT_DEFAULT));

        new utmPonderPlugin().register();
    }

    @SubscribeEvent
    public static void onGuiInit(ScreenEvent.Init.Post event) {
        if (!ToastReady && event.getScreen() instanceof TitleScreen) {
            ToastReady = true;
            tryToastPopup();
        }
    }

    @SubscribeEvent
    public static void onToast(ToastDisplaySignal event) {
        utm.LOGGER.warn("[UTM] Toast recieved");
        tryToastPopup();
    }

    public static void tryToastPopup() {
        utm.LOGGER.warn("[UTM] Toast popup attempted w/ flag {}", ToastTarget);
        if (ToastTarget) {
            ToastTarget = false;

            CompletableFuture.runAsync(() -> Minecraft.getInstance().getToasts().addToast(new UpdateToast(VersionTarget)), CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS));
            utm.LOGGER.warn("[UTM] Toast ran");
        }
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(utmMenus.GLINT_MENU.get(), GlintScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterBuffers(RegisterRenderBuffersEvent event) {
        event.registerRenderBuffer(utmRenderTypes.ADDITIVE_GLINT_ITEM.get());
        event.registerRenderBuffer(utmRenderTypes.OVERLAY_GLINT_ITEM.get());
        event.registerRenderBuffer(utmRenderTypes.ADDITIVE_GLINT_ENTITY.get());
        event.registerRenderBuffer(utmRenderTypes.OVERLAY_GLINT_ENTITY.get());

        PlanetRenderer.register();
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
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
    }

    @SubscribeEvent
    public static void registerCurioRenderers(FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(AllItems.COPPER_BACKTANK.get(), BacktankCurioRenderer::new);
        CuriosRendererRegistry.register(AllItems.NETHERITE_BACKTANK.get(), BacktankCurioRenderer::new);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(CitywallsBlockEntityRenderer.CWL);
        event.register(CitywallsBlockEntityRenderer.OWM);
        event.register(CitywallsBlockEntityRenderer.OWS);
        event.register(CitywallsBlockEntityRenderer.CWS);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                utmBlockEntities.CITYWALLS_METAL.get(),
                CitywallsBlockEntityRenderer::new
        );

        event.registerBlockEntityRenderer(
                utmBlockEntities.OXYGEN_COLLECTOR.get(),
                OxygenCollectorRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath("utm", "2313ag"), new DimensionSpecialEffects(Float.NaN, false, DimensionSpecialEffects.SkyType.NONE, false, false) {
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
        });
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
            private static final ResourceLocation FLOWING = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");

            @Override
            public @NotNull ResourceLocation getStillTexture() { return STILL; }
            @Override
            public @NotNull ResourceLocation getFlowingTexture() { return FLOWING; }
            @Override
            public int getTintColor() { return 0xFFB3E5FC; }
        }, utmFluids.LIQUID_OXYGEN_TYPE);
    }
}
