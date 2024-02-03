package com.harleylizard.retro.tasks;

import com.harleylizard.retro.RetroLoaderPlugin;
import cuchaz.enigma.Enigma;
import cuchaz.enigma.EnigmaProject;
import cuchaz.enigma.ProgressListener;
import cuchaz.enigma.classprovider.ClasspathClassProvider;
import cuchaz.enigma.source.Decompilers;
import cuchaz.enigma.translation.mapping.serde.MappingFormat;
import cuchaz.enigma.translation.mapping.serde.MappingParseException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public class CreateSourcesTasks extends DefaultTask {
    @TaskAction
    public void createSources() throws IOException, MappingParseException {
        var enigma = Enigma.builder().build();

        var progressListener = new ProgressListener() {
            @Override
            public void init(int i, String s) {}

            @Override
            public void step(int i, String s) {}
        };

        var project = getProject();

        var cache = RetroLoaderPlugin.getCache(project);
        var jarPath = cache.resolve("client.jar");
        var enigmaProject = enigma.openJar(jarPath, new ClasspathClassProvider(), progressListener);

        var mappingsPath = cache.resolve("mappings.txt");
        var mappings = MappingFormat.PROGUARD.read(mappingsPath, progressListener, enigma.getProfile().getMappingSaveParameters());
        enigmaProject.setMappings(mappings);

        var export = enigmaProject.exportRemappedJar(progressListener);

        var sourcesPath = cache.resolve("client-deobf-sources.jar");
        export.decompile(progressListener, Decompilers.CFR, EnigmaProject.DecompileErrorStrategy.TRACE_AS_SOURCE).write(sourcesPath, progressListener);
    }
}
