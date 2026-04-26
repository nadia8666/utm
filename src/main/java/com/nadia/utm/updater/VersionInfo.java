package com.nadia.utm.updater;


import net.neoforged.fml.ModList;

public class VersionInfo {
    private static final String COMMIT_ID;

    static {
        COMMIT_ID = ModList.get()
                .getModContainerById("utm")
                .map(container -> {
                    Object val = container.getModInfo().getModProperties().get("gitHash");
                    return val != null ? val.toString() : "unknown";
                })
                .orElse("unknown");
    }

    public static String commit() {
        return COMMIT_ID;
    }
}