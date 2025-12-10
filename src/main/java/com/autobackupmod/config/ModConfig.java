package com.autobackupmod.config;

import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    public boolean enabled = true;
    public String interval = "1h";
    public long lastBackup = 0;
    public List<String> worlds = new ArrayList<>();
    public int maxBackupsPerWorld = 10;
}
