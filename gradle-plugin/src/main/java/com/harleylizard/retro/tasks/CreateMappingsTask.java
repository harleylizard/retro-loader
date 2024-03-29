package com.harleylizard.retro.tasks;

import com.harleylizard.retro.RetroLoaderPlugin;
import com.harleylizard.retro.mappings.Mappings;
import com.harleylizard.retro.mappings.MappingsFormat;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;

public class CreateMappingsTask extends DefaultTask {
    @TaskAction
    public void createMappings() throws IOException {
        var cache = RetroLoaderPlugin.getCache(getProject());
        var path = cache.resolve("mappings.txt");

        try (var writer = Files.newBufferedWriter(path)) {
            var mappings = Mappings.deserialize("mappings.json");

            MappingsFormat.PROGUARD.getWriter().write(cache, writer, mappings);
            writer.flush();
        }
    }
}
