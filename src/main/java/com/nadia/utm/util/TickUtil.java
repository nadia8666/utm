package com.nadia.utm.util;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEventHost;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ForceLoad
public class TickUtil {
    public record Task(long tick, Runnable runnable) {
    }

    public static Map<ResourceKey<Level>, List<Task>> SERVER_TARGETS = new HashMap<>();
    public static Map<ResourceKey<Level>, List<Task>> CLIENT_TARGETS = new HashMap<>();

    public static void runIn(int ticks, Runnable runnable, Level level) {
        Task task = new Task(level.getGameTime() + ticks, runnable);

        Map<ResourceKey<Level>, List<Task>> targetMap = level.isClientSide() ? CLIENT_TARGETS : SERVER_TARGETS;
        targetMap.computeIfAbsent(level.dimension(), (a) -> new ArrayList<>()).add(task);
    }

    static {
        utmEventHost.register(LevelTickEvent.Post.class, event -> {
            Level level = event.getLevel();
            Map<ResourceKey<Level>, List<Task>> targetMap = level.isClientSide() ? CLIENT_TARGETS : SERVER_TARGETS;

            List<Task> tasks = targetMap.get(level.dimension());
            if (tasks == null || tasks.isEmpty()) return;

            long current = level.getGameTime();
            for (int i = tasks.size() - 1; i >= 0; i--) {
                Task task = tasks.get(i);
                if (current >= task.tick) {
                    tasks.remove(i);
                    task.runnable.run();
                }
            }
        });
    }
}
