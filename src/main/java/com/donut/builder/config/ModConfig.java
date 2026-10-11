package com.donut.builder.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance()
        .getConfigDir().resolve("donutbuilder.json");

    public int placeRateLimit = 7;
    public float rotationGCD = 0.15f;
    public boolean antiFlagRotation = true;
    public boolean antiFlagSequence = true;
    public boolean antiFlagRate = true;
    public int reachMarginTenths = 1;
    public boolean showGhostBlocks = true;
    public int ghostColor = 0x80FF00FF;
    public boolean buildWithFriends = false;
    public String friendKey = "";
    public int layerDelayMs = 400;

    public static ModConfig INSTANCE = load();

    public static ModConfig load() {
        if (PATH.toFile().exists()) {
            try (Reader r = new FileReader(PATH.toFile())) {
                ModConfig c = GSON.fromJson(r, ModConfig.class);
                if (c != null) return c;
            } catch (Exception ignored) {}
        }
        ModConfig c = new ModConfig();
        c.save();
        return c;
    }

    public void save() {
        try (Writer w = new FileWriter(PATH.toFile())) {
            GSON.toJson(this, w);
        } catch (Exception ignored) {}
    }
}
