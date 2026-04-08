package com.nadia.utm;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.nadia.utm.updater.AutoUpdater;
import com.nadia.utm.updater.ToastDisplaySignal;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static com.nadia.utm.updater.AutoUpdater.ToastTarget;
import static com.nadia.utm.updater.AutoUpdater.VersionTarget;

@EventBusSubscriber(modid = "utm")
public class utmCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utm_server")
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
        );
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utm")
                .then(Commands.literal("update").executes(context -> {
                    AutoUpdater.checkForUpdate();

                    return 1;
                }))
                .then(Commands.literal("reinstall").executes(context -> {
                    AutoUpdater.checkForUpdate(true);

                    return 1;
                }))
                .then(Commands.literal("stop").executes(context -> {
                    var mc = Minecraft.getInstance();
                    Minecraft.getInstance().execute(mc::stop);

                    return 1;
                }))
                .then(Commands.literal("test").then(Commands.argument("test_type", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(new String[]{"update_toast"}, builder)).executes(context -> {
                            switch (StringArgumentType.getString(context, "test_type")) {
                                case "update_toast": {
                                    ToastTarget = true;
                                    VersionTarget = "TEST VERSION";
                                    NeoForge.EVENT_BUS.post(new ToastDisplaySignal());
                                }
                            }

                            return 1;
                        }))
                )
        );
    }
}
