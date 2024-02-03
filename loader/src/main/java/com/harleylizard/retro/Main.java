package com.harleylizard.retro;

import net.minecraft.client.Minecraft;

import java.io.IOException;

public final class Main {

    private Main() {}

    public static void main(String[] args) throws IOException {
        RetroLoader retroLoader = new RetroLoader();
        retroLoader.load(Environment.COMMON);

        Minecraft.main(args);
    }
}
