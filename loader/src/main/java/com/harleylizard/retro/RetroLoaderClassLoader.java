package com.harleylizard.retro;

public final class RetroLoaderClassLoader extends ClassLoader {

    public RetroLoaderClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public Class<?> defineClass(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}
