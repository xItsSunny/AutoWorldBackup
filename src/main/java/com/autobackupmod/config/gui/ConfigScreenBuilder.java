package com.autobackupmod.config.gui;

import com.autobackupmod.config.ConfigManager;
import com.autobackupmod.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;
import java.util.*;

public class ConfigScreenBuilder {

    public static Screen build(Screen parent) {
        ModConfig cfg = ConfigManager.config;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Auto Backup Config"));

        builder.setSavingRunnable(ConfigManager::save);

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entry = builder.entryBuilder();

        general.addEntry(entry.startBooleanToggle(Text.literal("Enabled"), cfg.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(val -> cfg.enabled = val)
                .build());

        general.addEntry(entry.startStrField(Text.literal("Interval (s/m/h/d/w)"), cfg.interval)
                .setDefaultValue("6h")
                .setSaveConsumer(val -> cfg.interval = val)
                .build());

        general.addEntry(entry.startIntField(Text.literal("Max Backups Per World"), cfg.maxBackupsPerWorld)
                .setDefaultValue(10)
                .setSaveConsumer(val -> cfg.maxBackupsPerWorld = Math.max(1, val))
                .build());

        ConfigCategory worldCat = builder.getOrCreateCategory(Text.literal("World Selection"));

        File savesDir = FabricLoader.getInstance().getGameDir().resolve("saves").toFile();
        List<String> allWorlds = new ArrayList<>();

        if (savesDir.exists()) {
            File[] dirs = savesDir.listFiles(File::isDirectory);
            if (dirs != null) {
                for (File dir : dirs) allWorlds.add(dir.getName());
            }
        }

        Set<String> selectedWorlds = new HashSet<>(cfg.worlds);

        for (String world : allWorlds) {
            boolean isSelected = selectedWorlds.contains(world);

            worldCat.addEntry(
                    entry.startBooleanToggle(Text.literal(world), isSelected)
                            .setSaveConsumer(val -> {
                                if (val) selectedWorlds.add(world);
                                else selectedWorlds.remove(world);
                            })
                            .build()
            );
        }

        builder.setSavingRunnable(() -> {
            cfg.worlds = new ArrayList<>(selectedWorlds);
            ConfigManager.save();
        });

        return builder.build();
    }
}
