/*
 * This file is part of Kingdoms.
 * 
 * Kingdoms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdoms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdoms.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms.Commands;

import io.dallen.Kingdoms.Faction;
import io.dallen.Kingdoms.PlayerData;
import io.dallen.Kingdoms.Util.PermissionManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
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
                    if(Faction.getFactions().get(pd.getFaction()) != null){
                        Faction.getFactions().get(pd.getFaction()).getOnlinePlayers();
                    }
                }
            }else{
                for(Player plr : Bukkit.getOnlinePlayers()){
                    sender.sendMessage(plr.getName());
                }
            }
        }else if(cmd.getName().equalsIgnoreCase("message") && args.length > 1){
            if(Bukkit.getPlayer(args[0]) != null){
                Player p = Bukkit.getPlayer(args[0]);
                String msg = "";
                List<String> msgs = Arrays.asList(args);
                msgs.remove(0);
                p.sendMessage("[" + sender.getName() + "] " + StringUtils.join(msgs, " "));
                Convos.put(sender, p);
                Convos.put(p, sender);
            }
        }else if(cmd.getName().equalsIgnoreCase("reply")){
            if(Convos.containsKey(sender)){
                Convos.get(sender).sendMessage("[" + sender.getName() + "]" + StringUtils.join(args, " "));
            }else{
                sender.sendMessage("No player to reply to");
            }
        }else if(cmd.getName().equalsIgnoreCase("info")){
            //send all sorts of info about the server
        }else if(cmd.getName().equalsIgnoreCase("help")){
            //quick and easy help message
        }else if(cmd.getName().equalsIgnoreCase("is")){
            //get info about roles and kingdoms and stuff
        }else{
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(cmd.getName().equalsIgnoreCase("self")){
                    p.sendMessage(PlayerData.getData(p).toString());
                }
            }
        }
        return false;
    }
}
