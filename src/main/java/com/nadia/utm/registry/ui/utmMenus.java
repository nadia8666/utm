package com.nadia.utm.registry.ui;

import com.nadia.utm.gui.GlintMenu;
import com.nadia.utm.gui.OxygenFurnaceMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class utmMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, "utm");

    public static final Supplier<MenuType<GlintMenu>> GLINT_MENU = MENUS.register("glint_menu", () -> IMenuTypeExtension.create(GlintMenu::new));
    public static final Supplier<MenuType<OxygenFurnaceMenu>> OXYGEN_FURNACE_MENU = MENUS.register("oxygen_furnace_menu", () -> IMenuTypeExtension.create(OxygenFurnaceMenu::new));
}
