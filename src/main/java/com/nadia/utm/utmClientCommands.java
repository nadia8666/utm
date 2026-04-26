package com.nadia.utm;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.updater.AutoUpdater;
import com.nadia.utm.updater.ToastDisplaySignal;
import com.nadia.utm.updater.VersionInfo;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;

import static com.nadia.utm.updater.AutoUpdater.ToastTarget;
import static com.nadia.utm.updater.AutoUpdater.VersionTarget;

@ForceLoad(dist = Dist.CLIENT)
public class utmClientCommands {
    static {
        utmEvents.register(RegisterClientCommandsEvent.class, event -> event.getDispatcher().register(Commands.literal("utm")
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
                .then(Commands.literal("version").executes(context -> {
                    context.getSource().sendSystemMessage(Component.literal("[UTM] utm Version " + utm.VERSION + "-" + VersionInfo.commit() + " Pending version " + VersionTarget));

                    return 1;
                }))
                .then(Commands.literal("test").then(Commands.argument("test_type", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(new String[]{"update_toast", "sable_seal_test"}, builder)).executes(context -> {
                            switch (StringArgumentType.getString(context, "test_type")) {
                                case "update_toast": {
                                    ToastTarget = true;
                                    VersionTarget = "TEST VERSION";
                                    NeoForge.EVENT_BUS.post(new ToastDisplaySignal());
                                }
                                case "sable_seal_test": {
                                    Player player = Minecraft.getInstance().player;
                                    SubLevel level = (SubLevel) SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(player);

                                    if (level != null) {
                                        level.getPlot().getLoadedChunks().forEach(c -> utm.LOGGER.info("[UTM] Chunk Data @{}: {}", c.getPos(), c.getChunk().getData(utmAttachments.SEALED_AIR).sealedBlocks()));
                                    }
                                }
                            }

                            return 1;
                        }))
                )
        ));
    }
}
