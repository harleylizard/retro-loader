package com.harleylizard.retro;

import java.util.Locale;

public enum Environment {
    CLIENT,
    SERVER,
    COMMON;

    public static Environment fromString(String string) {
        switch (string.toLowerCase(Locale.ROOT)) {
            case "common":
                return COMMON;
            case "client":
                return CLIENT;
            case "server":
                return SERVER;
            default:
                throw new RuntimeException("Unknown environment");
        }
    }
}
