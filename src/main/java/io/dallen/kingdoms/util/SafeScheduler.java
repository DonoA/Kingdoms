package io.dallen.kingdoms.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

@AllArgsConstructor
public class SafeScheduler {

    @Getter
    private final BukkitScheduler scheduler;

    @Getter
    private final Plugin plugin;

    public BukkitTask runTask(Runnable runnable) {
        if (!plugin.isEnabled()) {
            return null;
        }

        return scheduler.runTask(plugin, runnable);
    }

}
