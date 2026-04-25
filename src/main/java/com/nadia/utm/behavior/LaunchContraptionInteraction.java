package com.nadia.utm.behavior;

import com.nadia.utm.client.ui.launch.LaunchScreen;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class LaunchContraptionInteraction extends SimpleBlockMovingInteraction {
    @OnlyIn(Dist.CLIENT)
    protected BlockState handle(Player player, Contraption contraption, BlockPos pos, BlockState currentState) {
        if (player.level().isClientSide())
            Minecraft.getInstance().setScreen(new LaunchScreen(Component.translatable("utm.gui.launch_contraption_menu"), contraption.entity.getId()));

        return currentState;
    }
}
