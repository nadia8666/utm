package com.nadia.utm.registry.attachment;

import com.mojang.serialization.Codec;
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
}
