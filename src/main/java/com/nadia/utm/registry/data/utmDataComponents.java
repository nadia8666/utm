package com.nadia.utm.registry.data;

import com.mojang.serialization.Codec;
import com.nadia.utm.renderer.glint.utmCodecs;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.joml.Vector2f;

public class utmDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "utm");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> GLINT_COLOR =
            COMPONENTS.register("glint_color", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> GLINT_ADDITIVE =
            COMPONENTS.register("glint_additive", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Vector2f>> GLINT_SPEED =
            COMPONENTS.register("glint_speed", () -> DataComponentType.<Vector2f>builder()
                    .persistent(utmCodecs.VECTOR2F)
                    .networkSynchronized(utmCodecs.VECTOR2F_STREAM)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Vector2f>> GLINT_SCALE =
            COMPONENTS.register("glint_scale", () -> DataComponentType.<Vector2f>builder()
                    .persistent(utmCodecs.VECTOR2F)
                    .networkSynchronized(utmCodecs.VECTOR2F_STREAM)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> GLINT_TYPE =
            COMPONENTS.register("glint_type", () -> DataComponentType.<ResourceLocation>builder()
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
                    .build());
}
