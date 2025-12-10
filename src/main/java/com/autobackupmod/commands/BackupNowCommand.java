package com.autobackupmod.commands;

import com.autobackupmod.core.WorldBackup;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class BackupNowCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("backupnow")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(BackupNowCommand::execute)
            );
        });
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        source.sendFeedback(() -> Text.literal("§e[AutoBackup] Starting backup now..."), false);
        
        source.getServer().execute(() -> {
            try {
                WorldBackup.run(source.getServer());
                source.sendFeedback(() -> Text.literal("§a[AutoBackup] Backup completed!"), false);
            } catch (Exception e) {
                source.sendFeedback(() -> Text.literal("§c[AutoBackup] Backup failed! Check logs."), false);
                e.printStackTrace();
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
