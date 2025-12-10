package com.autobackupmod;

import net.fabricmc.api.ModInitializer;
import com.autobackupmod.config.ConfigManager;
import com.autobackupmod.core.BackupScheduler;
import com.autobackupmod.commands.BackupNowCommand;

public class AutoBackupMod implements ModInitializer {
	public static final String MODID = "auto_backup";

	@Override
	public void onInitialize() {
		ConfigManager.load();
		BackupScheduler.init();
		BackupNowCommand.register();
	}
}
