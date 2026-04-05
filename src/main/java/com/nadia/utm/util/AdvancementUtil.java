package com.nadia.utm.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public class AdvancementUtil {
    public static void AwardAdvancement(ServerPlayer player, ResourceLocation advancement) {
        ServerAdvancementManager manager = Objects.requireNonNull(player.getServer()).getAdvancements();
        AdvancementHolder holder = manager.get(advancement);
        if (holder != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
            if (!progress.isDone())
                for (String crit : progress.getRemainingCriteria()) player.getAdvancements().award(holder, crit);
        }
    }
}
