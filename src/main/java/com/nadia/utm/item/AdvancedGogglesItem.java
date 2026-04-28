package com.nadia.utm.item;

import com.nadia.utm.registry.item.utmItems;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Optional;

public class AdvancedGogglesItem extends GogglesItem {
    public AdvancedGogglesItem(Properties properties) {
        super(properties);
    }

    private static Optional<Map<String, ICurioStacksHandler>> resolveCuriosMap(LivingEntity entity) {
        return Optional.ofNullable(entity.getCapability(CuriosCapability.INVENTORY)).map(ICuriosItemHandler::getCurios);
    }

    static {
        GogglesItem.addIsWearingPredicate(p -> resolveCuriosMap(p)
                .map(curiosMap -> {
                    for (ICurioStacksHandler stacksHandler : curiosMap.values()) {
                        int slots = stacksHandler.getSlots();
                        for (int slot = 0; slot < slots; slot++) {
                            if (utmItems.GOGGLES.isIn(stacksHandler.getStacks().getStackInSlot(slot))) {
                                return true;
                            }
                        }
                    }

                    return false;
                })
                .orElse(false));

        GogglesItem.addIsWearingPredicate(p -> utmItems.GOGGLES.isIn(p.getItemBySlot(EquipmentSlot.HEAD)));
    }
}
