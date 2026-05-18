package com.nadia.utm.util;

import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TickUtil {
    public record Task(long tick, Runnable runnable) {
    }

    public static Map<Level, List<Task>> TARGETS = new HashMap<>();

    public static void runIn(int ticks, Runnable runnable, Level level) {
        List<Task> tasks = TARGETS.computeIfAbsent(level, (a) -> new ArrayList<>());
        tasks.add(new Task(level.getGameTime() + ticks, runnable));
    }
}
