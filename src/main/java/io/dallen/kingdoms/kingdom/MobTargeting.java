package io.dallen.kingdoms.kingdom;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class MobTargeting {

    private final Plugin plugin;

    public static void startTargeting(Plugin plugin) {
        var spawning = new MobTargeting(plugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, spawning::checkMobs, 20, 20);
    }

    public void checkMobs() {

    }
}
