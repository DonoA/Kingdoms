/*
 * Copyright 2016 Donovan Allen
 *
 * This file is part of Kingdoms for the Morphics Network.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.kingdoms.essentials.Commands;

import io.dallen.kingdoms.utilities.Utils.PermissionManager;
import io.dallen.kingdoms.utilities.Utils.TimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class ModerationCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (sender.hasPermission(PermissionManager.getModPermission())) {
            if (cmd.getName().equalsIgnoreCase("where") && args.length > 0) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    Location ploc = Bukkit.getPlayer(args[0]).getLocation();
                    sender.sendMessage(ploc.getWorld().getName() + ", (" + ploc.getBlockX() + ", " + ploc.getBlockY() + ", " + ploc.getBlockZ() + ")");
                }
            } else if (cmd.getName().equalsIgnoreCase("ban") && args.length > 0) {
                if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(args[0])) {
                    String reason = StringUtils.join(ArrayUtils.remove(args, 0), " ");
                    Bukkit.getBanList(BanList.Type.NAME).addBan(args[0], reason, null, sender.getName());
                } else {
                    sender.sendMessage("That player is already banned!");
                }
            } else if (cmd.getName().equalsIgnoreCase("unban") && args.length > 1) {
                if (Bukkit.getBanList(BanList.Type.NAME).isBanned(args[0])) {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(args[0]);
                } else {
                    sender.sendMessage("That player is not banned!");
                }
            } else if (cmd.getName().equalsIgnoreCase("ipban") && args.length > 1) {
                if (!Bukkit.getBanList(BanList.Type.IP).isBanned(args[0])) {
                    String reason = StringUtils.join(ArrayUtils.remove(args, 0), " ");
                    Bukkit.getBanList(BanList.Type.IP).addBan(args[0], reason, null, sender.getName());
                } else {
                    sender.sendMessage("That player is already banned!");
                }
            } else if (cmd.getName().equalsIgnoreCase("unipban") && args.length > 1) {
                if (Bukkit.getBanList(BanList.Type.IP).isBanned(args[0])) {
                    Bukkit.getBanList(BanList.Type.IP).pardon(args[0]);
                } else {
                    sender.sendMessage("That player is not banned!");
                }
            } else if (cmd.getName().equalsIgnoreCase("kick") && args.length > 1) {
                if (Bukkit.getPlayer(args[0]) != null) {
                    String reason = StringUtils.join(ArrayUtils.remove(args, 0), " ");
                    Bukkit.getPlayer(args[0]).kickPlayer(reason);
                }
            } else if (cmd.getName().equalsIgnoreCase("tmpban") && args.length > 0) {
                if (!Bukkit.getBanList(BanList.Type.NAME).isBanned(args[0])) {
                    String reason = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ");
                    Bukkit.getBanList(BanList.Type.NAME).addBan(args[0], reason,
                            new Date(System.currentTimeMillis() + TimeUtil.getDate(args[1]).getTime()), sender.getName());
                } else {
                    sender.sendMessage("That player is already banned!");
                }
            } else if (cmd.getName().equalsIgnoreCase("uuid") && args.length > 0) {
                if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
                    sender.sendMessage(args[0] + " - " + Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString());
                } else {
                    sender.sendMessage("Player not found!");
                }
            } else if (cmd.getName().equalsIgnoreCase("fjail") && args.length > 0) {
                //Jail player through Jail system
            } else if (cmd.getName().equalsIgnoreCase("broadcast") && args.length > 0) {
                Bukkit.broadcastMessage(sender.getName() + ": " + StringUtils.join(args, " "));
            } else {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (cmd.getName().equalsIgnoreCase("invspy") && args.length > 0) {
                        if (Bukkit.getPlayer(args[0]) != null) {
                            Player plr = Bukkit.getPlayer(args[0]);
                            p.openInventory(plr.getInventory());
                        } else {
                            p.sendMessage("Player not found");
                        }
                    } else if (cmd.getName().equalsIgnoreCase("vanish")) {
                        //vanish
                    }
                }
            }
            return false;
        } else {
            sender.sendMessage("Not allowed!");
            return true;
        }
    }
}
