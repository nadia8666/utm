package com.nadia.utm.block.entity;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

@ForceLoad
public class BiomeSealerBlockEntity extends AbstractSealerBlockEntity {
    @Override
    public int getMaxVolume() {
        return 10000;
    }

    @Override
    public int getDraw() {
        double draw;

        if (SYNCED_VOLUME <= 500) {
            draw = 4 + (SYNCED_VOLUME / 500.0) * 16;
        } else if (SYNCED_VOLUME <= 1500) {
            draw = 20 + ((SYNCED_VOLUME - 500) / 1000.0) * 180;
        } else {
            draw = 200 + ((SYNCED_VOLUME - 1500) / 8500.0) * 800;
        }

        return (int) Math.min(draw, 1000);
    }

    public BiomeSealerBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.BIOME_SEALER.get(), pos, state);
    }

    @Override
    public boolean shouldStep() {
        if (Math.abs(getSpeed()) < 128) {
            if (ACTIVE) {
                ACTIVE = false;
                sendData();
                unseal();
            }
            return false;
        }

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);

        return true;
    }

    @Override
    public boolean isSpeedRequirementFulfilled() {
        return Mth.abs(getSpeed()) >= 128;
    }

    static {
        utmEvents.register(RegisterCapabilitiesEvent.class, (event) -> event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                utmBlockEntities.BIOME_SEALER.get(),
                (be, side) -> be.CAPABILITY
        ));
    }
}
