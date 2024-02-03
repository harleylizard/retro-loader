package com.harleylizard.retro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.harleylizard.retro.util.ThrowableConsumer;
import com.harleylizard.retro.util.ThrowablePredicate;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RetroLoader {
    private final Path path = Util.createDirectory("mods");

    private final List<Entrypoint> entrypoints = new ArrayList<>();

    public void load(Environment environment) throws IOException {
        RetroLoaderClassLoader classLoader = new RetroLoaderClassLoader(RetroLoader.class.getClassLoader());

        Gson gson = new GsonBuilder().registerTypeAdapter(Mod.class, (JsonDeserializer<Mod>) Mod::deserialize).create();

        for (Path path : collectFiles()) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(path, classLoader)) {
                Optional<Mod> optional = createMod(gson, fileSystem);
                if (!optional.isPresent()) {
                    continue;
                }

                Mod mod = optional.get();
                try (Stream<Path> stream = Files.walk(fileSystem.getPath("/")).filter(Files::isRegularFile).filter(file -> file.toString().endsWith(".class"))) {
                    stream.forEach(ThrowableConsumer.wrap(file -> {
                        byte[] bytes = Files.readAllBytes(file);

                        String name = file.toString();
                        name = name.substring(1, name.lastIndexOf(".class")).replace("/", ".");

                        classLoader.defineClass(name, bytes);
                    }));
                }

                List<String> list = mod.getEntryPoints().get(environment);
                for (String className : list) {
                    try {
                        Class<?> loadedClass = classLoader.loadClass(className);
                        Constructor<?> constructor = loadedClass.getConstructor();

                        Entrypoint entrypoint = (Entrypoint) constructor.newInstance();
                        entrypoints.add(entrypoint);
                    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Optional<Mod> createMod(Gson gson, FileSystem fileSystem) throws IOException {
        String fileName = "mod.json";
        Path path = fileSystem.getPath(fileName);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return Optional.of(gson.fromJson(reader, Mod.class));
        }
    }

    private List<Path> collectFiles() throws IOException {
        try (Stream<Path> stream = Files.walk(path, FileVisitOption.FOLLOW_LINKS).filter(path -> ThrowablePredicate.wrap(this::isZip).test(path))) {
            return stream.collect(Collectors.toList());
        }
    }

    private boolean isZip(Path path) throws IOException {
        if (Files.isRegularFile(path) && Files.size(path) >= 4) {
            byte[] bytes = Files.readAllBytes(path);
            return bytes[0] == 0x50 && bytes[1] == 0x4B && bytes[2] == 0x03 && bytes[3] == 0x04;
        }
        return false;
    }
}
