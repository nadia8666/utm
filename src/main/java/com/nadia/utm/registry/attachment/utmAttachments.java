package com.nadia.utm.registry.attachment;

import com.mojang.serialization.Codec;
import com.nadia.utm.behavior.space.SealedChunkData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class utmAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "utm");

    public static final Supplier<AttachmentType<Boolean>> ENTERED_2313AG = ATTACHMENTS.register(
            "entered_2313ag",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build()
    );

    public static final Supplier<AttachmentType<Integer>> TEMPORARY_OXYGEN = ATTACHMENTS.register(
            "temporary_oxygen",
            () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<SealedChunkData>> SEALED_AIR = ATTACHMENTS.register(
            "sealed_air",
            () -> AttachmentType.builder(SealedChunkData::new).build()
    );
}
