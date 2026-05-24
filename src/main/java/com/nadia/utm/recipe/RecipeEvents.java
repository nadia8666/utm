package com.nadia.utm.recipe;

import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.item.utmItems;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import static com.nadia.utm.config.utmServerConfig.HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT;

@EventBusSubscriber(modid = "utm")
public class RecipeEvents {
    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (left.is(Items.ANVIL) && right.is(Items.NETHERITE_INGOT) && right.getCount() >= 1) {
            event.setOutput(new ItemStack(utmBlocks.HEAVY_METAL_ANVIL.ITEM.get()));
            event.setMaterialCost(1);
            event.setCost(HEAVY_METAL_ANVIL_LEVEL_REQUIREMENT.getAsInt());
        }

        if (left.is(utmTools.COPPER_THROWING_SPEAR.get()) && right.is(utmItems.COPPER_PLATING.get())) {
            if (!left.isDamaged()) {
                return;
            }

            ItemStack output = left.copy();
            int currentDamage = left.getDamageValue();
            int maxDamage = left.getMaxDamage();
            int ingotsNeeded = 0;
            int repairedDamage = currentDamage;

            while (repairedDamage > 0 && ingotsNeeded < right.getCount()) {
                repairedDamage -= 10;
                ingotsNeeded++;
            }

            if (repairedDamage < 0)
                repairedDamage = 0;

            output.setDamageValue(repairedDamage);
            event.setOutput(output);
            event.setMaterialCost(ingotsNeeded);
            event.setCost(1);
        }

        if (left.is(utmTools.TNT_THROWING_SPEAR.get()) && right.is(Items.ENCHANTED_BOOK)) {
            ItemEnchantments bookEnchants = EnchantmentHelper.getEnchantmentsForCrafting(right);
            if (bookEnchants.isEmpty())
                return;

            ItemStack output = left.copy();

            boolean applied = false;
            int cost = 0;

            for (Object2IntMap.Entry<Holder<Enchantment>> entry : bookEnchants.entrySet()) {
                Holder<Enchantment> enchantment = entry.getKey();
                int level = entry.getIntValue();

                if (enchantment.value().canEnchant(output) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantmentsForCrafting(left).keySet(), enchantment)) {
                    int finalLevel = Math.min(level, enchantment.value().getMaxLevel());
                    output.enchant(enchantment, finalLevel);

                    cost += finalLevel * 2;
                    applied = true;
                }
            }

            if (applied) {
                event.setOutput(output);
                event.setMaterialCost(1);
                event.setCost(Math.max(1, cost));
            }
        }
    }
}
