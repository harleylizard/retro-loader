package com.harleylizard.retro.mappings;

import com.harleylizard.retro.JarUtil;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public final class ReobfuscatedTinyWriter implements MappingsWriter {
    @Override
    public void write(Path cache, Writer writer, Mappings<Mappings<String>> mappings) throws IOException {
        var map = mappings.map();

        writer.write("v1\ta\tb\n");

        for (var entry : JarUtil.getObfuscated(cache).entrySet()) {
            String obfuscated = entry.getKey();

            if (!map.containsKey(obfuscated)) {
                String namespace = mappings.name().replace(".", "/");

                var format = "CLASS\t%s/%s\t%s\n".formatted(namespace, obfuscated, obfuscated);
                writer.write(format);
                continue;
            }

            var obfuscatedMapping = map.get(obfuscated);
            var name = obfuscatedMapping.name().replace(".", "/");

            var format = "CLASS\t%s\t%s\n".formatted(name, obfuscated);
            writer.write(format);
        }
    }
}
