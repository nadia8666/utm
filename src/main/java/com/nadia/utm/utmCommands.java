package com.nadia.utm;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.updater.AutoUpdater;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.nadia.utm.updater.AutoUpdater.VersionTarget;

@ForceLoad()
public class utmCommands {
    static {
        utmEvents.register(RegisterCommandsEvent.class, event -> event.getDispatcher().register(Commands.literal("utm_server")
                .then(Commands.literal("update").executes(context -> {
                    AutoUpdater.checkForUpdate();

                    return 1;
                }))
                .then(Commands.literal("reinstall").executes(context -> {
                    AutoUpdater.checkForUpdate(true);

                    return 1;
                }))
                .then(Commands.literal("restart").executes(context -> {
                    var server = context.getSource().getServer();
                    context.getSource().sendSuccess(() -> Component.literal("[UTM] Closing server..."), true);

                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        player.connection.disconnect(Component.literal("Restarting server!"));
                    }

                    server.halt(false);

                    return 1;
                }))
                .then(Commands.literal("version").executes(context -> {
                    context.getSource().sendSystemMessage(Component.literal("[UTM] utm Version " + utm.VERSION + " Pending version " + VersionTarget));

                    return 1;
                }))
        ));
    }
}
