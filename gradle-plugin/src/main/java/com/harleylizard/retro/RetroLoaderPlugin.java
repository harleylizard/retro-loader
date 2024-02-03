package com.harleylizard.retro;

import com.harleylizard.retro.tasks.*;
import cuchaz.enigma.translation.mapping.serde.MappingParseException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class RetroLoaderPlugin implements Plugin<Project> {
    private static final String PLUGIN_NAME = "retro-loader";

    @Override
    public void apply(Project target) {
        target.getPluginManager().apply(JavaPlugin.class);

        var extensions = target.getExtensions();
        var javaExtension = extensions.getByType(JavaPluginExtension.class);
        javaExtension.setSourceCompatibility(JavaVersion.VERSION_1_8);
        javaExtension.setTargetCompatibility(JavaVersion.VERSION_1_8);

        var repositories = target.getRepositories();
        repositories.maven(repository -> repository.setUrl("https://maven.fabricmc.net/"));
        repositories.maven(repository -> repository.setUrl("https://libraries.minecraft.net/"));
        repositories.maven(repository -> repository.setUrl("https://repo.spongepowered.org/repository/maven-public/"));

        extensions.create(PLUGIN_NAME, RetroLoaderExtension.class);

        configureTasks(target);
    }

    private void configureTasks(Project project) {
        var tasks = project.getTasks();
        var downloadMinecraft = tasks.register("downloadMinecraft", DownloadMinecraftTask.class, downloadMinecraftTask -> {
            downloadMinecraftTask.setGroup(PLUGIN_NAME);
        });

        var createMappings = tasks.register("createMappings", CreateMappingsTask.class, createMappingsTask -> {
            createMappingsTask.dependsOn(downloadMinecraft);
            createMappingsTask.setGroup(PLUGIN_NAME);
        });

        tasks.register("deobfuscateMinecraft", DeobfuscateMinecraftTask.class, deobfuscateMinecraftTask -> {
            deobfuscateMinecraftTask.dependsOn(createMappings);
            deobfuscateMinecraftTask.setGroup(PLUGIN_NAME);
        });

        tasks.register("createSources", CreateSourcesTasks.class, createSourcesTasks -> {
            createSourcesTasks.dependsOn(createMappings);
            createSourcesTasks.setGroup(PLUGIN_NAME);
        });

        tasks.register("createReobfuscationMappings", CreateReobfuscationMappingsTask.class, createReobfuscationMappingsTask -> {
            createReobfuscationMappingsTask.setGroup(PLUGIN_NAME);
        });

        tasks.register("reobfuscateBuildJar", ReobfuscateBuildJarTask.class, reobfuscateBuildJarTask -> {
            reobfuscateBuildJarTask.dependsOn(tasks.getByName("createReobfuscationMappings"));
            reobfuscateBuildJarTask.dependsOn(tasks.getByName("build"));
            reobfuscateBuildJarTask.setGroup(PLUGIN_NAME);
        });

        tasks.getByName("build").doLast(task -> {
            try {
                ((CreateReobfuscationMappingsTask) tasks.getByName("createReobfuscationMappings")).createReobfuscationMappings();
                ((ReobfuscateBuildJarTask) tasks.getByName("reobfuscateBuildJar")).reobfuscateBuildJar();
            } catch (IOException e) {
                project.getLogger().error("", e);
            }
        });

        try {
            ((DownloadMinecraftTask) tasks.getByName("downloadMinecraft")).downloadMinecraftJar();
            ((CreateMappingsTask) tasks.getByName("createMappings")).createMappings();
            ((DeobfuscateMinecraftTask) tasks.getByName("deobfuscateMinecraft")).deobfuscateMinecraftJar();
        } catch (IOException | MappingParseException e) {
            project.getLogger().error("", e);
        }
    }

    public static Path getCache(Project project) throws IOException {
        var path = Paths.get(project.getLayout().getBuildDirectory().get().toString()).resolve("cache");
        if (!Files.isDirectory(path)) {
            Files.createDirectories(path);
        }
        return path;
    }
}
