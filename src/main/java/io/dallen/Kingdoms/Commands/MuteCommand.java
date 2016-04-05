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

import io.dallen.Kingdoms.PlayerData;
import io.dallen.Kingdoms.Util.PermissionManager;
import io.dallen.Kingdoms.Util.TimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    
    @NoArgsConstructor
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
