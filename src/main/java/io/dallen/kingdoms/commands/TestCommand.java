package io.dallen.kingdoms.commands;

import io.dallen.kingdoms.packets.BlockBreakAnimator;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        var player = (Player) commandSender;
        var creator = new WorldCreator(args[0]);
        var targetWorld = Bukkit.createWorld(creator);
        player.teleport(targetWorld.getSpawnLocation());
//        var block = player.getTargetBlockExact(10);
//
//        if (block == null) {
//            return true;
//        }
//
//        var count = Float.parseFloat(args[0]);
//        BlockBreakAnimator.doDamage(block.getLocation(), count, false);

        return true;
    }
}
