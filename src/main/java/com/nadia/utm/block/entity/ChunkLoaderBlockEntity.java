package com.nadia.utm.block.entity;

import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.server.ChunkLoadHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChunkLoaderBlockEntity extends BlockEntity {
    public ChunkLoaderBlockEntity(BlockPos pos, BlockState blockState) {
        super(utmBlockEntities.CHUNK_LOADER.get(), pos, blockState);
    }

    public UUID SOURCE;

    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            setLoaded(serverLevel, true);
        }
    }

    public void setLoaded(ServerLevel serverLevel, boolean force) {
        if (SOURCE != null) {
            this.checkLoadStatePlayer(serverLevel);
            return;
        }

        ChunkPos chunkPos = new ChunkPos(this.worldPosition);
        ChunkLoadHandler.CONTROLLER.forceChunk(serverLevel, this.worldPosition, chunkPos.x, chunkPos.z, force, true);
        ChunkLoadHandler.addLoader(this.worldPosition, this);
    }

    @Override
    public void setRemoved() {
        ChunkLoadHandler.removeLoader(this.worldPosition, this);
        super.setRemoved();
    }

    public void checkLoadStatePlayer(ServerLevel level) {
        boolean online = level.getServer().getPlayerList().getPlayer(this.SOURCE) != null;

        ChunkPos chunkPos = new ChunkPos(this.worldPosition);
        ChunkLoadHandler.CONTROLLER.forceChunk(level, SOURCE, chunkPos.x, chunkPos.z, online, true);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (SOURCE != null) {
            tag.putUUID("SOURCE", SOURCE);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("SOURCE")) {
            this.SOURCE = tag.getUUID("SOURCE");
        }
    }
}
