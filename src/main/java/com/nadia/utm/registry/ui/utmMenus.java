package com.nadia.utm.registry.ui;

import com.nadia.utm.ui.glint.GlintMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class utmMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, "utm");

    public static final Supplier<MenuType<GlintMenu>> GLINT_MENU = MENUS.register("glint_menu", () -> IMenuTypeExtension.create(GlintMenu::new));
}
