package com.nadia.utm.behavior;

import com.nadia.utm.block.LaunchContraptionBlock;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.utm;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class LaunchContraptionInteraction extends SimpleBlockMovingInteraction {
    protected BlockState handle(Player player, Contraption contraption, BlockPos pos, BlockState currentState) {
        utm.LOGGER.info("[UTM] attempting to interact! {}", player.level().isClientSide());
        if (!player.level().isClientSide()) {
            LaunchContraptionBlock LaunchContraption = utmBlocks.LAUNCH_CONTRAPTION.block.get();
            player.openMenu(new SimpleMenuProvider(LaunchContraption, LaunchContraption.getDisplayName()), pos);
        }

        return currentState;
    }
}
