package com.harleylizard.retro;

import java.io.IOException;
import java.io.InputStream;

public final class Resources {

    private Resources() {}

    public static InputStream getResource(String path) throws IOException {
        return Resources.class.getClassLoader().getResource(path).openStream();
    }
}
