package io.dallen.kingdoms.update;

import io.dallen.kingdoms.Kingdoms;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UpdateCommand implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        commandSender.sendMessage("Rebuilding");
        var proc = Runtime.getRuntime().exec("./update_kingdoms.sh");
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String s;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
        var result = proc.waitFor();
        commandSender.sendMessage("Rebuilt, return code " + result);
        if (result != 0) {
            commandSender.sendMessage("Build failed, exiting");
            return true;
        }

        Bukkit.dispatchCommand(commandSender, "reload");
        return true;
    }
}
