package com.nadia.utm.datagen.providers;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class utmModelProvider extends BlockStateProvider {
   public utmModelProvider(PackOutput output, ExistingFileHelper helper) {
       super(output, "utm", helper);
   }


    @Override
    protected void registerStatesAndModels() {

    }
}
