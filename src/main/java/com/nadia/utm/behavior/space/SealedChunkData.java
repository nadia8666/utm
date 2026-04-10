package com.nadia.utm.behavior.space;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.HashMap;
import java.util.Map;

public record SealedChunkData(Map<BlockPos, BlockPos> sealedBlocks) {
    public static final Codec<SealedChunkData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(BlockPos.CODEC, BlockPos.CODEC)
                            .fieldOf("sealed_blocks")
                            .forGetter(SealedChunkData::sealedBlocks)
            ).apply(instance, SealedChunkData::new)
    );

    public SealedChunkData(IAttachmentHolder holder) {
        this(new HashMap<>());
    }
}
