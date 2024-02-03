package com.harleylizard.retro.tasks;

import com.harleylizard.retro.RetroLoaderPlugin;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class DownloadMinecraftTask extends DefaultTask {
    @TaskAction
    public void downloadClientJar() throws IOException {
        var path = RetroLoaderPlugin.getCache(getProject()).resolve("client.jar");
        if (Files.exists(path)) {
            return;
        }

        var url = new URL("https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar");
        try (var inputStream = url.openStream(); var dataOutputStream = new DataOutputStream(Files.newOutputStream(path))) {
            byte[] bytes = new byte[1024];

            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                dataOutputStream.write(bytes, 0, read);
            }
            dataOutputStream.flush();
        }
    }
}
