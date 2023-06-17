package io.dallen.kingdoms;

import io.dallen.kingdoms.update.UpdateCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Kingdoms extends JavaPlugin {

    public static Kingdoms instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("update").setExecutor(new UpdateCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
