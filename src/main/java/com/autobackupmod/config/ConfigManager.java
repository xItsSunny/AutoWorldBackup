package com.autobackupmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class ConfigManager {
    public static ModConfig config = new ModConfig();

    public static void load() {
        File file = FabricLoader.getInstance().getConfigDir().resolve("auto_backup.json").toFile();
        if (!file.exists()) {
            save();
            return;
        }
        try (Reader reader = new FileReader(file)) {
            config = new Gson().fromJson(reader, ModConfig.class);
            if (config == null) config = new ModConfig();
        } catch (Exception e) {
            e.printStackTrace();
            config = new ModConfig();
            save();
        }
    }

    public static void save() {
        File file = FabricLoader.getInstance().getConfigDir().resolve("auto_backup.json").toFile();
        try (Writer writer = new FileWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(config, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
