package com.nadia.utm.event;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class utmEvents {
    private static IEventBus BUS;
    private static final Set<Class<? extends Event>> HOOKED_EVENTS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Map<Class<? extends Event>, List<Consumer<?>>> CALLBACKS = new ConcurrentHashMap<>();

    public static void setup(IEventBus bus) {
        utmEvents.BUS = bus;
    }

    public static <E extends Event> void register(Class<E> eventClass, Consumer<E> callback) {
        if (HOOKED_EVENTS.add(eventClass))
            addEvent(eventClass);

        CALLBACKS.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(callback);
    }

    private static <E extends Event> void addEvent(Class<E> eventClass) {
        if (BUS == null)
            throw new IllegalStateException("[UTM] Attempted to hook into " + eventClass.getName() + " (@" + eventClass + ") before the mod bus was loaded! If you see this something has gone terribly wrong.");

        if (IModBusEvent.class.isAssignableFrom(eventClass))
            BUS.addListener(EventPriority.NORMAL, false, eventClass, utmEvents::fire);
        else
            NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventClass, utmEvents::fire);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Event> void fire(E event) {
        List<Consumer<?>> targets = CALLBACKS.get(event.getClass());
        if (targets != null)
            for (Consumer<?> consumer : targets)
                ((Consumer<E>) consumer).accept(event);
    }
}
