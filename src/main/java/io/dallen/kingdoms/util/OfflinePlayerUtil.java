package io.dallen.kingdoms.util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class OfflinePlayerUtil {

    public static void trySendMessage(OfflinePlayer player, String message) {
        ifOnline(player, (p) -> p.sendMessage(message));
    }

    public static void ifOnline(OfflinePlayer player, Consumer<Player> exec) {
        if (player.isOnline()) {
            exec.accept(player.getPlayer());
        }
    }

}
