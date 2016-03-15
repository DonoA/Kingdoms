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

import io.dallen.Kingdoms.PlayerData;
import io.dallen.Kingdoms.Util.PermissionManager;
import io.dallen.Kingdoms.Util.TimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class MuteCommand implements CommandExecutor{
    
    @Getter
    private static HashMap<Player, ArrayList<Player>> localMutes = new HashMap<Player, ArrayList<Player>>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        Player m;
        if(args.length > 0 && (m = Bukkit.getPlayer(args[0])) != null){
            if(sender.hasPermission(PermissionManager.getModPermission()) && args.length > 1 && 
                    (args[1].equalsIgnoreCase("global") || args[1].equalsIgnoreCase("g"))){
                PlayerData pd = PlayerData.getData(m);
                if(pd.getMuted() != null){
                    pd.setMuted(null);
                    sender.sendMessage("You have unmuted " + args[0]);
                }else{
                    if(args.length == 2){
                        pd.setMuted(new MuteClass(args[1]));
                        sender.sendMessage("You have muted " + args[0] + " for " + args[1]);
                        m.sendMessage("You have been muted for " + args[1]);
                    }else if(args.length == 3){
                        pd.setMuted(new MuteClass(args[1], TimeUtil.getDate(args[2])));
                        sender.sendMessage("You have muted " + args[0] + " for " + args[1] + " for " + args[2]);
                        m.sendMessage("You have been muted for " + args[1] + " for " + args[2]);
                    }
                }
            }else{
                if(sender instanceof Player){
                    Player p = (Player) sender;
                    PlayerData pd = PlayerData.getData(p);
                    if(localMutes.containsKey(m)){
                        if(localMutes.get(m).contains(p)){
                            localMutes.get(m).remove(m);
                            p.sendMessage("You have unmuted " + args[0]);
                        }else{
                            localMutes.get(m).add(m);
                            p.sendMessage("You have muted " + args[0]);
                        }
                    }else{
                        localMutes.put(m, new ArrayList<Player>(Arrays.asList(new Player[] {p})));
                        p.sendMessage("You have muted " + args[0]);
                    }
                }else{
                     sender.sendMessage("Console can only mute global");
                }
            }
            
        }else{
            sender.sendMessage("Player not found");
        }
        return true;
    }
    
    public static class MuteClass {
        
        @Getter @Setter
        private String reason;
        
        @Getter @Setter
        private Date time;
        
        public MuteClass(String r){
            reason = r;
        }
        
        public MuteClass(String r, Date time){
            reason = r;
            this.time = time;
        }
    }
}
