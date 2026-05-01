package com.nadia.utm.event.events;

import com.nadia.utm.networking.payloads.GetOxygenPayload;
import net.neoforged.bus.api.Event;

public class OxygenPayloadEvent extends Event {
    public final GetOxygenPayload PAYLOAD;

    public OxygenPayloadEvent(GetOxygenPayload payload) {
        this.PAYLOAD = payload;
    }
}
