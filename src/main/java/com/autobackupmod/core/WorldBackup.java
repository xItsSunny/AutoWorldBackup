package com.autobackupmod.core;

import com.autobackupmod.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WorldBackup {

    public static void run(MinecraftServer server) {
        File savesDir = FabricLoader.getInstance().getGameDir().resolve("saves").toFile();
        Path backupDir = FabricLoader.getInstance().getGameDir().resolve("backups");

        try {
            Files.createDirectories(backupDir);
        } catch (IOException e) {
            sendToAllPlayers(server, "§c[AutoBackup] Failed to create backup folder!");
            e.printStackTrace();
            return;
        }

        try {
            server.getPlayerManager().saveAllPlayerData();
            server.save(false, true, true);
        } catch (Exception e) {
            sendToAllPlayers(server, "§c[AutoBackup] Failed to flush world data!");
            e.printStackTrace();
        }

        for (String world : ConfigManager.config.worlds) {
            if (world == null || world.isBlank()) continue;
            File worldDir = new File(savesDir, world);
            if (!worldDir.exists()) continue;

            long timestamp = System.currentTimeMillis();
            Path zipPath = backupDir.resolve(world + "_" + timestamp + ".zip");

            sendToAllPlayers(server, "§e[AutoBackup] Backing up §6" + world + "§e…");

            List<Path> files = new ArrayList<>();
            try {
                Files.walk(worldDir.toPath()).forEach(path -> {
                    if (!Files.isDirectory(path)) files.add(path);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            int total = Math.max(1, files.size());
            int index = 0;

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                for (Path path : files) {
                    Path rel = worldDir.toPath().relativize(path);
                    ZipEntry entry = new ZipEntry(rel.toString());
                    zos.putNextEntry(entry);
                    try {
                        Files.copy(path, zos);
                    } catch (IOException e) {
                        sendToAllPlayers(server, "§7[AutoBackup] Skipped locked file: " + rel);
                    }
                    zos.closeEntry();
                    index++;
                    if (index % 100 == 0 || index == total) {
                        int percent = (int) ((index / (double) total) * 100);
                        sendToAllPlayers(server, "§7[AutoBackup] " + world + "… " + percent + "%");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            deleteOldBackups(world, backupDir.toFile(), ConfigManager.config.maxBackupsPerWorld);
            sendToAllPlayers(server, "§a[AutoBackup] Backup completed for §6" + world);
        }
    }

    private static void deleteOldBackups(String world, File backupDir, int maxBackups) {
        File[] backups = backupDir.listFiles((dir, name) ->
                name.startsWith(world + "_") && name.endsWith(".zip")
        );

        if (backups == null || backups.length <= maxBackups) return;

        Arrays.sort(backups, Comparator.comparingLong(File::lastModified));
        int remove = backups.length - maxBackups;
        for (int i = 0; i < remove; i++) {
            try {
                backups[i].delete();
            } catch (Exception ignored) {}
        }
    }

    private static void sendToAllPlayers(MinecraftServer server, String message) {
        server.getPlayerManager().getPlayerList().forEach(player ->
                player.sendMessage(Text.literal(message))
        );
    }
}
