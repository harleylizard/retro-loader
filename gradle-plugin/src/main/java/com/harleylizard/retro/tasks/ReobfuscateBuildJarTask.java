package com.harleylizard.retro.tasks;

import com.harleylizard.retro.RetroLoaderPlugin;
import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public class ReobfuscateBuildJarTask extends DefaultTask {
    @TaskAction
    public void reobfuscateBuildJar() throws IOException {
        var project = getProject();
        var path = RetroLoaderPlugin.getCache(project).resolve("mappings-reobfuscated.txt");

        var remapper = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(path, "a", "b"))
                .build();

        var jarName = "%s-%s.jar".formatted(project.getName(), project.getVersion().toString());

        var buildDir = project.getLayout().getBuildDirectory().getAsFile().get().toPath();
        var jarPath = buildDir.resolve("libs").resolve(jarName);

        try (var outputConsumer = new OutputConsumerPath.Builder(jarPath).build()) {
            outputConsumer.addNonClassFiles(jarPath, NonClassCopyMode.FIX_META_INF, remapper);

            remapper.readInputs(jarPath);
            remapper.readClassPath(jarPath);

            remapper.apply(outputConsumer);
        } catch (Exception e) {
            getLogger().error("", e);
        } finally {
            remapper.finish();
        }
    }
}
