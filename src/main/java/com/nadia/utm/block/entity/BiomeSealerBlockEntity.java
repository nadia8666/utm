package com.nadia.utm.block.entity;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@ForceLoad()
public class BiomeSealerBlockEntity extends AbstractSealerBlockEntity {
    public static int MAX_VOLUME = 10000;

    public BiomeSealerBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.BIOME_SEALER.get(), pos, state);
    }

    static {
        utmEvents.register(RegisterCapabilitiesEvent.class, (event) -> event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                utmBlockEntities.BIOME_SEALER.get(),
                (be, side) -> be.CAPABILITY
        ));
    }
}
