package com.nadia.utm.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;

public interface IContraptionNBTAccessor {
    void utm$writeAllData(RegistryFriendlyByteBuf buf);
}
