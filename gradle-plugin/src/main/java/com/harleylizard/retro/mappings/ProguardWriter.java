package com.harleylizard.retro.mappings;

import com.harleylizard.retro.JarUtil;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public final class ProguardWriter implements MappingsWriter {
    @Override
    public void write(Path cache, Writer writer, Mappings<Mappings<String>> mappings) throws IOException {
        var map = mappings.map();

        writer.write("#\n");
        for (var entry : JarUtil.getObfuscated(cache).entrySet()) {
            String obfuscated = entry.getKey();

            if (!map.containsKey(obfuscated)) {
                String namespace = mappings.name();

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
    }
}
