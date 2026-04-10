package com.nadia.utm.networking;

import com.nadia.utm.client.ui.TabMenuLayer;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.behavior.space.SpaceStateHandler;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.gui.GlintMenu;
import com.nadia.utm.networking.payloads.DropGravePayload;
import com.nadia.utm.networking.payloads.GlintSyncPayload;
import com.nadia.utm.networking.payloads.LaunchContraptionPayload;
import com.nadia.utm.networking.payloads.TabLayerPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

// TODO: refactor
@ForceLoad
public class utmNetworking {
    private static PayloadRegistrar REGISTRAR;

    public static <P extends CustomPacketPayload> void server(PacketDef<P> def, IPayloadHandler<P> consumer) {
        REGISTRAR.playToServer(def.type(), def.codec(), consumer);
    }

    public static <P extends CustomPacketPayload> void client(PacketDef<P> def, IPayloadHandler<P> consumer) {
        REGISTRAR.playToClient(def.type(), def.codec(), consumer);
    }

    public static void registerNetworkingEvents(final RegisterPayloadHandlersEvent event) {
        REGISTRAR = event.registrar("1");


        server(DropGravePayload.DEF, (payload, context) -> context.enqueueWork(() -> DropGravePayload.drop(payload, context)));
        server(GlintSyncPayload.DEF, (payload, context) -> context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof GlintMenu menu) menu.unpack(payload);
        }));

        server(LaunchContraptionPayload.DEF, (payload, context) -> context.enqueueWork(() -> SpaceStateHandler.launchRecieved(payload, context)));
        client(TabLayerPayload.DEF, (payload, context) -> context.enqueueWork(() -> TabMenuLayer.CACHE = payload.players()));
    }

    static {
        utmEvents.register(RegisterPayloadHandlersEvent.class, utmNetworking::registerNetworkingEvents);
    }
}