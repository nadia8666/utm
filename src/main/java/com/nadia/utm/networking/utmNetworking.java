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
import com.nadia.utm.networking.payloads.debug.RequestSealedDataPayload;
import com.nadia.utm.networking.payloads.debug.SyncSealedDataPayload;
import com.nadia.utm.registry.attachment.utmAttachments;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

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

        server(RequestSealedDataPayload.DEF, (payload, context) -> context.enqueueWork(() -> {
            Level level = context.player().level();
            ChunkAccess chunk = level.getChunk(payload.pos().x, payload.pos().z);
            if (chunk.hasData(utmAttachments.SEALED_AIR)) {
                context.reply(new SyncSealedDataPayload(payload.pos(), chunk.getData(utmAttachments.SEALED_AIR)));
            }
        }));

        client(SyncSealedDataPayload.DEF, (payload, context) -> context.enqueueWork(() -> {
            Level level = net.minecraft.client.Minecraft.getInstance().level;
            if (level != null) {
                var chunk = level.getChunk(payload.pos().x, payload.pos().z);
                chunk.setData(utmAttachments.SEALED_AIR, payload.data());
            }
        }));
    }

    static {
        utmEvents.register(RegisterPayloadHandlersEvent.class, utmNetworking::registerNetworkingEvents);
    }
}