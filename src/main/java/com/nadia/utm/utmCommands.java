package com.nadia.utm;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.updater.AutoUpdater;
import com.nadia.utm.updater.VersionInfo;
import com.nadia.utm.util.SableUtil;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;
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
                    MinecraftServer server = context.getSource().getServer();
                    context.getSource().sendSuccess(() -> Component.literal("[UTM] Closing server..."), true);

                    server.execute(() -> server.halt(false));

                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        player.connection.disconnect(Component.literal("Restarting server!"));
                    }

                    return 1;
                }))
                .then(Commands.literal("version").executes(context -> {
                    context.getSource().sendSystemMessage(Component.literal("[UTM] utm Version " + utm.VERSION + "-" + VersionInfo.commit() + " Pending version " + VersionTarget));

                    return 1;
                }))
                .then(Commands.literal("to_space").executes(context -> {
                    Player player = context.getSource().getPlayer();
                    if (player == null || FMLEnvironment.production) return 0;

                    ServerSubLevel level = (ServerSubLevel) SableUtil.getSublevel(player);
                    MinecraftServer server = player.getServer();
                    if (server == null) return 0;

                    if (level != null) {
                        ServerLevel target = player.level().dimension().equals(utmDimensions.AG_KEY) ? server.getLevel(ServerLevel.OVERWORLD) : server.getLevel(utmDimensions.AG_KEY);
                        ServerLevel origin = (ServerLevel) player.level();

                        server.execute(() -> SableUtil.DimensionController.changeDimension(level, origin, target));
                    }

                    return 1;
                }))
        ));
    }
}
