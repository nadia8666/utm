package com.nadia.utm_updater.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLPaths;
import org.checkerframework.checker.units.qual.Current;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class AutoUpdater {
    public static String CurrentVersion = "0.0.0-INTERNAL";
    public static void CheckForUpdate() throws ExecutionException, InterruptedException, RuntimeException, IOException {
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

            if (!tryMigrateVersion(version) || !Objects.equals(version, CurrentVersion)) {
                JsonArray assets = json.get("assets").getAsJsonArray();
                JsonObject element = assets.get(0).getAsJsonObject();
                startUpdate(element.get("browser_download_url").getAsString(), version);
            }
        } else {
            throw new RuntimeException("UTM Version check failed: " + response.statusCode());
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
                        throw new RuntimeException("HTTP Error: " + response.statusCode());
                    }
                });
    }

    private static void startUpdate(String downloadUrl, String latest) throws ExecutionException, InterruptedException {
        Path modsFolder = FMLPaths.MODSDIR.get();
        Path targetPath = modsFolder.resolve(formatPath(latest));
        Path file = downloadUpdate(downloadUrl, targetPath).get();

        SystemToast.add(
                Minecraft.getInstance().getToasts(),
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                Component.literal("Update Installed!"),
                Component.literal("UTM Version " + latest + " has been downloaded, please restart Minecraft!")
        );
    }

    private static boolean tryMigrateVersion(String version) throws IOException {
        Path modsFolder = FMLPaths.MODSDIR.get();

        Path currentFile = modsFolder.resolve(CurrentVersion+".jar");
        AtomicReference<Path> updateFile = new AtomicReference<>();
        AtomicReference<Path> oldFile = new AtomicReference<>();

        Files.list(modsFolder).forEach(file -> {
            if (file.endsWith(SUFFIX)) {
                updateFile.set(file);
            } else if (file.endsWith(SUFFIX+".old")) {
                oldFile.set(file);
            }
        });

        if (updateFile.get() != null) {
            Files.move(currentFile, modsFolder.resolve(formatPath(CurrentVersion)+".old"), StandardCopyOption.REPLACE_EXISTING);
            Files.move(updateFile.get(), modsFolder.resolve(version+".jar"), StandardCopyOption.REPLACE_EXISTING);

            return true;
        }

        if (oldFile.get() != null) {
            try {
                Files.deleteIfExists(oldFile.get());
            } catch (Exception ignored) {}
        }

        return false;
    }

    private static String formatPath(String version) {
        return version + ".jar" + SUFFIX;
    }

    public static String SUFFIX = ".utm_update";
}
