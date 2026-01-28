package com.nadia.utm;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = "utm")
public class utmCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utm_restart")
                .requires(source -> source.hasPermission(4))
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
    }
}
