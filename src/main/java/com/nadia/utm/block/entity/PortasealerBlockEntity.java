package com.nadia.utm.block.entity;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@ForceLoad()
public class PortasealerBlockEntity extends AbstractSealerBlockEntity {
    @Override
    public int getMaxVolume() {
        return 100;
    }

    public PortasealerBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.PORTASEALER.get(), pos, state);
    }

    static {
        utmEvents.register(RegisterCapabilitiesEvent.class, (event) -> event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                utmBlockEntities.PORTASEALER.get(),
                (be, side) -> be.CAPABILITY
        ));
    }
}
