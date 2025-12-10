package com.autobackupmod.core;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import com.autobackupmod.config.ConfigManager;
import com.autobackupmod.config.ModConfig;

public class BackupScheduler {
    private static long nextBackup;
    private static boolean backupRunning = false;

    public static void init() {
        scheduleNext();
        ServerTickEvents.END_SERVER_TICK.register(BackupScheduler::tick);
    }

    private static void tick(MinecraftServer server) {
        ModConfig cfg = ConfigManager.config;
        if (!cfg.enabled || backupRunning) return;

        long now = System.currentTimeMillis();
        if (now >= nextBackup) {
            backupRunning = true;
            try {
                WorldBackup.run(server);
                cfg.lastBackup = now;
                ConfigManager.save();
            } finally {
                backupRunning = false;
                scheduleNext();
            }
        }
    }

    private static void scheduleNext() {
        nextBackup = System.currentTimeMillis() +
                IntervalParser.toMillis(ConfigManager.config.interval);
    }
}
