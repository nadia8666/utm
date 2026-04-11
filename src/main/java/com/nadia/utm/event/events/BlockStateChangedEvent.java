package com.nadia.utm.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;

public class BlockStateChangedEvent extends Event {
    public final Level Level;
    public final BlockPos Pos;
    public final BlockState OldState;
    public final BlockState NewState;

    public BlockStateChangedEvent(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        this.Level = level;
        this.Pos = pos;
        this.OldState = oldState;
        this.NewState = newState;
    }
}
