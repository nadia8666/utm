package com.nadia.utm.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nadia.utm.utm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AutoUpdater {
    public static String CurrentVersion = "0.0.0-INTERNAL";
    public static void CheckForUpdate() throws ExecutionException, InterruptedException, RuntimeException {
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

            utm.LOGGER.warn("UTM LATEST VERSION: {}",version);
            utm.LOGGER.warn("UTM CURRENT VERSION: {}",CurrentVersion);
            utm.LOGGER.warn("UTM TARGET UPDATE: {}", !Objects.equals(version, CurrentVersion));
        } else {
            throw new RuntimeException("UTM Version check failed: " + response.statusCode());
        }
    }
}
