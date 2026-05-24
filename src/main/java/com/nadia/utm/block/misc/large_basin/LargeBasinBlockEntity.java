package com.nadia.utm.block.misc.large_basin;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEventHost;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.lang.reflect.Field;
import java.util.List;

@ForceLoad
public class LargeBasinBlockEntity extends BasinBlockEntity {
    private static final Field INGREDIENT_ROTATION_FIELD;
    private static final Field VISUALIZED_OUTPUT_ITEMS_FIELD;

    static {
        try {
            INGREDIENT_ROTATION_FIELD = BasinBlockEntity.class.getDeclaredField("ingredientRotation");
            INGREDIENT_ROTATION_FIELD.setAccessible(true);

            VISUALIZED_OUTPUT_ITEMS_FIELD = BasinBlockEntity.class.getDeclaredField("visualizedOutputItems");
            VISUALIZED_OUTPUT_ITEMS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("missing largebasinblockentity fields", e);
        }
    }

    public LargeBasinBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.LARGE_BASIN.get(), pos, state);
    }

    public IItemHandlerModifiable getItemCapability() {
        return itemCapability;
    }

    public LerpedFloat getIngredientRotation() {
        try {
            return (LerpedFloat) INGREDIENT_ROTATION_FIELD.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<IntAttached<ItemStack>> getVisualizedOutputItems() {
        try {
            return (List<IntAttached<ItemStack>>) VISUALIZED_OUTPUT_ITEMS_FIELD.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        utmEventHost.register(RegisterCapabilitiesEvent.class, (event) -> {
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    utmBlockEntities.LARGE_BASIN.get(),
                    (be, context) -> be.itemCapability
            );
            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    utmBlockEntities.LARGE_BASIN.get(),
                    (be, context) -> be.fluidCapability
            );
        });
    }
}
