package com.harleylizard.retro.tasks;

import com.harleylizard.retro.RetroLoaderPlugin;
import com.harleylizard.retro.mappings.Mappings;
import com.harleylizard.retro.mappings.MappingsFormat;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;

public class CreateReobfuscationMappingsTask extends DefaultTask {
    @TaskAction
    public void createReobfuscationMappings() throws IOException {
        var cache = RetroLoaderPlugin.getCache(getProject());
        var path = cache.resolve("mappings-reobfuscated.txt");

        try (var writer = Files.newBufferedWriter(path)) {
            var mappings = Mappings.deserialize("mappings.json");

            MappingsFormat.REOBFUSCATED_TINY.getWriter().write(cache, writer, mappings);
            writer.flush();
        }
    }
}
