package com.harleylizard.retro;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record Mapping<T>(String name, Map<String, T> map) {

    public static Mapping<Mapping<String>> deserialize(String path) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(Resources.getResource(path)))) {
            var gson = new GsonBuilder().create();

            var json = gson.fromJson(reader, JsonElement.class);
            return deserialize(json);
        }
    }

    private static Mapping<Mapping<String>> deserialize(JsonElement json) {
        var jsonObject = json.getAsJsonObject();

        var deobfuscated = jsonObject.getAsJsonObject("deobfuscated");

        var map = new HashMap<String, Mapping<String>>(deobfuscated.size());
        for (var entry : deobfuscated.entrySet()) {
            map.put(entry.getKey(), deserializeEntry(entry.getValue()));
        }

        var namespace = jsonObject.getAsJsonPrimitive("namespace").getAsString();
        return new Mapping<>(namespace, Collections.unmodifiableMap(map));
    }

    private static Mapping<String> deserializeEntry(JsonElement json) {
        if (json.isJsonPrimitive()) {
            var name = json.getAsJsonPrimitive().getAsString();
            return new Mapping<>(name, Collections.emptyMap());
        }

        JsonObject jsonObject = json.getAsJsonObject();

        var fields = jsonObject.getAsJsonObject("fields");

        var map = new HashMap<String, String>(fields.size());
        for (var entry : fields.entrySet()) {
            var value = entry.getValue().getAsJsonPrimitive().getAsString();
            map.put(entry.getKey(), value);
        }

        var deobfuscated = jsonObject.getAsJsonPrimitive("deobfuscated").getAsString();
        return new Mapping<>(deobfuscated, Collections.unmodifiableMap(map));
    }
}
