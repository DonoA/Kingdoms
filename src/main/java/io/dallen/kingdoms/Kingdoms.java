package io.dallen.kingdoms;

import io.dallen.kingdoms.update.UpdateCommand;
import io.dallen.kingdoms.worldgen.WorldGenCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Kingdoms extends JavaPlugin {

    public static Kingdoms instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("update").setExecutor(new UpdateCommand());
        getCommand("genworld").setExecutor(new WorldGenCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
