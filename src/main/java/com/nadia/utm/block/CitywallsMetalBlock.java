package com.nadia.utm.block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;

public class CitywallsMetalBlock extends Block implements IWrenchable, SimpleWaterloggedBlock {

    public CitywallsMetalBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any());
    }



}
