package com.harleylizard.retro.tasks;

import com.harleylizard.retro.JarUtil;
import com.harleylizard.retro.Mapping;
import com.harleylizard.retro.RetroLoaderPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;

public class CreateMappingsTask extends DefaultTask {
    @TaskAction
    public void createMappings() throws IOException {
        var cache = RetroLoaderPlugin.getCache(getProject());
        var path = cache.resolve("mappings.txt");
        if (Files.exists(path)) {
            return;
        }

        try (var writer = Files.newBufferedWriter(path)) {
            var mapping = Mapping.deserialize("mappings.json");

            var map = mapping.map();

            writer.write("#\n");
            for (var entry : JarUtil.getObfuscated(cache).entrySet()) {
                String obfuscated = entry.getKey();

                if (!map.containsKey(obfuscated)) {
                    String namespace = mapping.name();;

                    var format = "%s.%s -> %s:\n".formatted(namespace, obfuscated, obfuscated);
                    writer.write(format);
                    continue;
                }

                var obfuscatedMapping = map.get(obfuscated);

                var format = "%s -> %s:\n".formatted(obfuscatedMapping.name(), obfuscated);
                writer.write(format);

                var fields = obfuscatedMapping.map();
                if (!fields.isEmpty()) {
                    for (var field : fields.entrySet()) {
                        var split = field.getValue().split(":", 2);

                        format = "    %s %s -> %s\n".formatted(split[0], split[1], field.getKey());
                        writer.write(format);
                    }
                }
            }
            writer.flush();
        }
    }
}
