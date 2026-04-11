package com.nadia.utm.behavior.space;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public record SealedChunkData(Map<BlockPos, BlockPos> sealedBlocks) {
    private record Entry(BlockPos key, BlockPos value) {
        static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                BlockPos.CODEC.fieldOf("k").forGetter(Entry::key),
                BlockPos.CODEC.fieldOf("v").forGetter(Entry::value)
        ).apply(i, Entry::new));
    }

    public static final Codec<SealedChunkData> CODEC = Entry.CODEC.listOf().xmap(
            list -> {
                Map<BlockPos, BlockPos> map = new HashMap<>();
                list.forEach(e -> map.put(e.key(), e.value()));
                return new SealedChunkData(map);
            },
            data -> data.sealedBlocks().entrySet().stream()
                    .map(e -> new Entry(e.getKey(), e.getValue()))
                    .toList()
    );

    @Nullable
    public BlockPos get(BlockPos target) {
        return sealedBlocks.get(target);
    }

    public SealedChunkData(IAttachmentHolder holder) {
        this(new HashMap<>());
    }
}