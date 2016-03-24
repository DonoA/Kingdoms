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
package io.dallen.Kingdoms.Commands;

import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.PlayerData;
import io.dallen.Kingdoms.Util.PermissionManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class GeneralCommands implements CommandExecutor{
    
    @Getter
    private static HashMap<CommandSender, CommandSender> Convos = new HashMap<CommandSender, CommandSender>();

        
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(cmd.getName().equalsIgnoreCase("list")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(p.hasPermission(PermissionManager.getModPermission()) && args.length > 0 && args[0].equalsIgnoreCase("all")){
                    for(Player plr : Bukkit.getOnlinePlayers()){
                        p.sendMessage(plr.getName());
                    }
                }else{
                    PlayerData pd = PlayerData.getData(p);
                    if(Kingdom.getKingdoms().get(pd.getKingdom()) != null){
                        Kingdom.getKingdoms().get(pd.getKingdom()).getOnlinePlayers();
                    }
                }
            }else{
                for(Player plr : Bukkit.getOnlinePlayers()){
                    sender.sendMessage(plr.getName());
                }
            }
            return true;
        }else if(cmd.getName().equalsIgnoreCase("message") && args.length > 1){
            if(Bukkit.getPlayer(args[0]) != null){
                Player p = Bukkit.getPlayer(args[0]);
                p.sendMessage("[" + sender.getName() + "] " + StringUtils.join(ArrayUtils.remove(args, 0), " "));
                Convos.put(sender, p);
                Convos.put(p, sender);
            }
            return true;
        }else if(cmd.getName().equalsIgnoreCase("reply")){
            if(Convos.containsKey(sender)){
                Convos.get(sender).sendMessage("[" + sender.getName() + "]" + StringUtils.join(args, " "));
            }else{
                sender.sendMessage("No player to reply to");
            }
            return true;
        }else if(cmd.getName().equalsIgnoreCase("info")){
            sender.sendMessage("§7Welcome to §6Kingdoms§7! "
                    + "If you are just starting out, find a kingdom's message board so that you can grab a contract. "
                    + "Contracts are basically jobs other players can offer, allowing each kingdom to set up its own economy. "
                    + "Some example contracts might be to infiltrate an enemy kingdom or build warehouses and other structures, "
                    + "and there are tons of other things that can be done with contracts.\n" 
                    + "\n" 
                    + "§6If you have any specific questions, feel free to ask in chat or check out our help section:\n" 
                    + "§7<forum link>");
            return true;
        }else if(cmd.getName().equalsIgnoreCase("help")){
            sender.sendMessage("§4--- §6Kingdoms §7Help Links §4---\n" 
                    + "\n" 
                    + "§6Chat with other players and staff on our forums!\n" 
                    + "§7<forum link>\n" 
                    + "§6Report issues or bugs:\n" 
                    + "§7<forum link>\n" 
                    + "§6Report player abuse or cheating:\n" 
                    + "§7<forum link>\n" 
                    + "§6Check out our wiki here for help!\n" 
                    + "§7<forum link>\n" 
                    + "§6Want more specific help? Check out our help section:\n" 
                    + "§7<forum link>");
            return true;
        }else if(cmd.getName().equalsIgnoreCase("is")){
            //get info from wiki database
            return true;
        }else{
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(cmd.getName().equalsIgnoreCase("self")){
                    p.sendMessage(PlayerData.getData(p).toString());
                    return true;
                }
            }else{
                //not player
                return true;
            }
        }
        return false;
    }
}
