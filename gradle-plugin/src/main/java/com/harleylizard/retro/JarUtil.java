package com.harleylizard.retro;

import org.objectweb.asm.ClassReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class JarUtil {

    private JarUtil() {}

    public static Map<String, String> getObfuscated(Path cache) throws IOException {
        var path = cache.resolve("client.jar");
        try (var zipInputStream = new ZipInputStream(Files.newInputStream(path))) {
            var map = new HashMap<String, String>();

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                var name = zipEntry.getName();
                if (!name.endsWith(".class") || name.contains("/")) {
                    continue;
                }

                var reader = new ClassReader(readBytes(zipInputStream));

                map.put(reader.getClassName(), reader.getSuperName());
            }

            return Collections.unmodifiableMap(map);
        }
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
            var bytes = new byte[1024];

            int read;
            while ((read = inputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, read);
            }

            byteArrayOutputStream.flush();

            return byteArrayOutputStream.toByteArray();
        }
    }
}
