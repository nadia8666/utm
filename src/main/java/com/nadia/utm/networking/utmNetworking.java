package com.nadia.utm.networking;

import com.nadia.utm.client.ui.TabMenuLayer;
import com.nadia.utm.ui.glint.GlintMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector2f;

@EventBusSubscriber(modid = "utm")
public class utmNetworking {
    @SubscribeEvent
    public static void registerNetworkingEvents(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(GlintSyncPayload.TYPE, GlintSyncPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof GlintMenu menu) {
                menu.COLOR = payload.color();
                menu.ADDITIVE = payload.additive();
                menu.SPEED = new Vector2f(payload.speed());
                menu.SCALE = new Vector2f(payload.scale());
                menu.TYPE = payload.glintType();

                menu.broadcastChanges();
            }
        }));

        registrar.playToClient(
                TabLayerPayload.TYPE,
                TabLayerPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        TabMenuLayer.CACHE = payload.players();
                    });
                }
        );
    }
}
