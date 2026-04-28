package com.nadia.utm.block.entity;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.utm;
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
        if (SYNCED_VOLUME <= 0) return 0;

        double draw = (int) Math.pow(1.0007, SYNCED_VOLUME);

        if (SYNCED_VOLUME < 1500) {
            double alpha = 0.5 * (Math.cos((Math.PI * SYNCED_VOLUME) / 1500.0) + 1.0);
            draw = (10 - Math.pow(SYNCED_VOLUME, 2) / 150000) * alpha + draw * (1 - alpha);
        }

        return (int) Math.min(draw + 20, 1000);
    }

    public BiomeSealerBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.BIOME_SEALER.get(), pos, state);
    }

    @Override
    public boolean shouldStep() {
        if (Math.abs(getSpeed()) < 128) {
            if (RECALC)
                process();

            if (ACTIVE) {
                ACTIVE = false;
                unseal();
                sendData();
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
