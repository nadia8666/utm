package com.nadia.utm.networking;

import com.nadia.utm.client.ui.TabMenuLayer;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.events.OxygenPayloadEvent;
import com.nadia.utm.event.events.SyncSealedDataEvent;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.gui.GlintMenu;
import com.nadia.utm.networking.payloads.*;
import com.nadia.utm.registry.attachment.utmAttachments;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ForceLoad
public class utmNetworking {
    public static PayloadRegistrar REGISTRAR;
    public static List<Runnable> CALLBACKS = new ArrayList<>();

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

        client(TabLayerPayload.DEF, (payload, context) -> context.enqueueWork(() -> TabMenuLayer.CACHE = payload.players()));

        server(RequestSealedDataPayload.DEF, (payload, context) -> context.enqueueWork(() -> {
            Level level = context.player().level();
            ChunkAccess chunk = level.getChunk(payload.pos().x, payload.pos().z);
            if (chunk.hasData(utmAttachments.SEALED_AIR)) {
                context.reply(new SyncSealedDataPayload(payload.pos(), chunk.getData(utmAttachments.SEALED_AIR)));
            }
        }));

        client(SyncSealedDataPayload.DEF, (payload, context) -> context.enqueueWork(() -> NeoForge.EVENT_BUS.post(new SyncSealedDataEvent(payload))));

        REGISTRAR.playBidirectional(
                GetOxygenPayload.TYPE,
                GetOxygenPayload.CODEC,
                (payload, context) -> {
                    if (context.flow() == PacketFlow.SERVERBOUND) {
                        context.enqueueWork(() -> {
                            UUID uuid = UUID.fromString(payload.id());
                            Player player = context.player();
                            if (player.level() instanceof ServerLevel level && player instanceof ServerPlayer sPlayer) {
                                Entity entity = level.getEntity(uuid);
                                if (entity != null) {
                                    Integer oxygen = entity.getExistingDataOrNull(utmAttachments.TEMPORARY_OXYGEN);
                                    if (oxygen == null)
                                        oxygen = -1;
                                    context.reply(new GetOxygenPayload(payload.id(), oxygen));
                                }
                            }
                        });
                    } else {
                        context.enqueueWork(() -> NeoForge.EVENT_BUS.post(new OxygenPayloadEvent(payload)));
                    }
                }
        );

        for (Runnable c : CALLBACKS)
            c.run();
    }

    public static void addCallback(Runnable c) {
        if (REGISTRAR != null)
            c.run();
        else
            CALLBACKS.add(c);
    }

    static {
        utmEvents.register(RegisterPayloadHandlersEvent.class, utmNetworking::registerNetworkingEvents);
    }
}