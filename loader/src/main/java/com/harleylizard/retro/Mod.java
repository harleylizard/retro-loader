package com.harleylizard.retro;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;

public final class Mod {
    private final Map<Environment, List<String>> entryPoints;

    private Mod(Map<Environment, List<String>> entryPoints) {
        this.entryPoints = entryPoints;
    }

    public Map<Environment, List<String>> getEntryPoints() {
        return entryPoints;
    }

    public static Mod deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonObject entryPoints = jsonObject.getAsJsonObject("entry-points");
        Map<Environment, List<String>> map = new HashMap<>(entryPoints.size());

        for (Map.Entry<String, JsonElement> entry : entryPoints.entrySet()) {
            Environment environment = Environment.fromString(entry.getKey());

            List<String> list = deserializeEntryPoints(entry.getValue());
            map.put(environment, Collections.unmodifiableList(list));
        }

        return new Mod(Collections.unmodifiableMap(map));
    }

    private static List<String> deserializeEntryPoints(JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            List<String> list = new ArrayList<>(jsonArray.size());

            for (JsonElement element : jsonArray) {
                list.add(element.getAsString());
            }
            return list;
        }
        return Collections.singletonList(jsonElement.getAsString());
    }
}
