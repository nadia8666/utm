package com.nadia.utm.registry.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector2f;

public class utmCodecs {
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, "utm");

    public static final Codec<Vector2f> VECTOR2F = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("x").forGetter(vec -> vec.x),
                    Codec.FLOAT.fieldOf("y").forGetter(vec -> vec.y)
            ).apply(instance, Vector2f::new)
    );

    public static final StreamCodec<ByteBuf, Vector2f> VECTOR2F_STREAM = StreamCodec.composite(
            ByteBufCodecs.FLOAT, vec -> vec.x,
            ByteBufCodecs.FLOAT, vec -> vec.y,
            Vector2f::new
    );

    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x,
            ByteBufCodecs.DOUBLE, Vec3::y,
            ByteBufCodecs.DOUBLE, Vec3::z,
            Vec3::new
    );

    public static final StreamCodec<ByteBuf, ChunkPos> CHUNK_POS_STREAM =
            ByteBufCodecs.VAR_LONG.map(ChunkPos::new, ChunkPos::toLong);

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Vector2f>> ENTITY_VECTOR2F = ENTITY_DATA_SERIALIZERS.register("entity_vector2f", () -> EntityDataSerializer.forValueType(VECTOR2F_STREAM));
}