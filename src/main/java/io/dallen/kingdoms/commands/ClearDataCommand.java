package io.dallen.kingdoms.commands;

import io.dallen.kingdoms.Kingdoms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ClearDataCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        commandSender.sendMessage("Cleaning");
        Kingdoms.instance.setClearData(true);
        Bukkit.dispatchCommand(commandSender, "reload");
        return true;
    }
}
