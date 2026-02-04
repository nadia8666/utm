package com.nadia.utm;

import com.nadia.utm.updater.AutoUpdater;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = "utm")
public class utmCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utm_restart")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    var server = context.getSource().getServer();
                    context.getSource().sendSuccess(() -> Component.literal("[UTM] Closing server..."), true);

                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        player.connection.disconnect(Component.literal("Restarting server!"));
                    }

                    server.halt(false);

                    return 1;
                })
        );

        event.getDispatcher().register(Commands.literal("utm_update_server")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    try {
                        AutoUpdater.checkForUpdate();
                    } catch (Exception ignored) {}

                    return 1;
                })
        );
    }

    @SubscribeEvent
    public static void onRegisteClientrCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utm_update_client")
                .requires(source -> source.hasPermission(0))
                .executes(context -> {
                    try {
                        AutoUpdater.checkForUpdate();
                    } catch (Exception ignored) {}

                    return 1;
                })
        );
    }
}
