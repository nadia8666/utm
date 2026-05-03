package com.nadia.utm.mixin.compat.create;

import com.nadia.utm.compat.IContraptionNBTAccessor;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.data.ContraptionSyncLimiting;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AbstractContraptionEntity.class, remap = false)
public abstract class AbstractContraptionEntityMixin extends Entity implements IContraptionNBTAccessor {
    public AbstractContraptionEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void writeAdditional(CompoundTag compound, HolderLookup.Provider registries, boolean spawnPacket);

    @Override
    public void utm$writeAllData(RegistryFriendlyByteBuf buf) {
        CompoundTag compound = new CompoundTag();
        writeAdditional(compound, buf.registryAccess(), false);
        ContraptionSyncLimiting.writeSafe(compound, buf);
    }
}
