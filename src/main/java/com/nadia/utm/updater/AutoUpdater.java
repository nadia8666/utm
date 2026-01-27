package com.nadia.utm.updater;

import com.nadia.utm.utm;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static net.neoforged.fml.loading.FMLLoader.getDist;


public class AutoUpdater {
    public static String CurrentVersion = "0.0.0-INTERNAL";
    public static void checkForUpdate() throws ExecutionException, InterruptedException, RuntimeException, IOException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.github.com/repos/nadia8666/utm/releases/latest"))
                .header("User-Agent", "utm-update")
                .header("Accept", "application/vnd.github.v3+json")
                .GET()
                .build();

        HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
        if (response.statusCode() == 200) {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String version = json.get("tag_name").getAsString();

            utm.LOGGER.warn("[UTM] Version check: {} {} {}", version, CurrentVersion, Objects.equals(version, CurrentVersion));

            if (!tryMigrateVersion() && !Objects.equals(version, CurrentVersion)) {
                JsonArray assets = json.get("assets").getAsJsonArray();
                AtomicReference<JsonObject> element = new AtomicReference<>();

                assets.forEach(target -> {
                    if (!target.getAsJsonObject().get("name").getAsString().contains("_updater")) {
                        element.set(target.getAsJsonObject());
                    }
                });

                utm.LOGGER.warn("[UTM] Starting update!");
                startUpdate(element.get().get("browser_download_url").getAsString(), version);
            }
        } else {
            tryMigrateVersion();
            throw new RuntimeException("[UTM] Version check failed: " + response.statusCode());
        }
    }

    private static CompletableFuture<Path> downloadUpdate(String downloadUrl, Path targetPath) {
        URI uri = URI.create(downloadUrl);

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("User-Agent", "utm-updater")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(targetPath))
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return response.body();
                    } else {
                        throw new RuntimeException("[UTM] HTTP Error: " + response.statusCode());
                    }
                });
    }


    public static boolean ToastReady = false;
    public static boolean ToastTarget = false;
    public static String VersionTarget = "v-0.0.0";
    private static void startUpdate(String downloadUrl, String latest) {
        Path modsFolder = FMLPaths.MODSDIR.get();

        Path currentFile = modsFolder.resolve("utm.jar");
        CompletableFuture.runAsync(() -> {
            try {
                Files.move(currentFile, modsFolder.resolve(formatPath()+".old"), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignored) {}

            Path targetPath = modsFolder.resolve("utm.jar");
            try {
                downloadUpdate(downloadUrl, targetPath).get();
            } catch (Exception ignored) {}

            utm.LOGGER.warn("[UTM] Update installed!");

            if (getDist() == Dist.CLIENT) {
                VersionTarget = "v" + latest;

                ToastTarget = true;
            }
        });
    }

    private static boolean tryMigrateVersion() throws IOException {
        Path modsFolder = FMLPaths.MODSDIR.get();
        AtomicReference<Path> oldFile = new AtomicReference<>();

        try (Stream<Path> entries = Files.list(modsFolder)) {
           entries.forEach(file -> {
                if (file.toString().contains(SUFFIX+".old")) {
                    oldFile.set(file);
                }
            });
        }

        if (oldFile.get() != null) {
            try {
                Files.deleteIfExists(oldFile.get());
            } catch (Exception ignored) {}
        }

        return false;
    }

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "utm-AutoUpdater");
        thread.setDaemon(true); // Ensures the thread stops when the JVM closes
        return thread;
    });

    public static void startAutoUpdate() {
        SCHEDULER.scheduleAtFixedRate(() -> {
            try {
                utm.LOGGER.info("[UTM] Checking for updates.");
                AutoUpdater.checkForUpdate();
            } catch (Exception ignored) {}
        }, 0, 30, TimeUnit.MINUTES);
    }

    private static String formatPath() {
        return "utm.jar" + SUFFIX;
    }

    public static String SUFFIX = ".utm_update";
}
