package com.nadia.utm.event.events;

import com.nadia.utm.networking.payloads.debug.SyncSealedDataPayload;
import net.neoforged.bus.api.Event;

public class SyncSealedDataEvent extends Event {
    public final SyncSealedDataPayload PAYLOAD;

    public SyncSealedDataEvent(SyncSealedDataPayload payload) {
        this.PAYLOAD = payload;
    }
}
