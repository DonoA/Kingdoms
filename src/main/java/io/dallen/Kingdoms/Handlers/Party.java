/*
 * This file is part of Kingdoms.
 * 
 * Kingdom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdom.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms.Handlers;

import io.dallen.Kingdoms.PlayerData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class Party{
    
    @Getter
    private static HashMap<String, Party> Parties = new HashMap<String, Party>();
    
    @Getter
    private Player Owner;
    
    @Getter
    private ArrayList<Player> Members = new ArrayList<Player>();
    
    @Getter
    private HashMap<Player, Date> Invites = new HashMap<Player, Date>();
    
    public Party(Player invite, Player owner){
        Owner = owner;
        Invites.put(invite, new Date());
    }
    
    public boolean join(Player p){
        if(Invites.containsKey(p)){
            if(Invites.get(p).after(new Date(System.currentTimeMillis() - (120000)))){ //less than 2 min ago
                Invites.remove(p);
                Members.add(p);
                return true;
            }else{
                Invites.remove(p);
            }
        }
        return false;
    }
    
    public static class PartyCommands implements CommandExecutor{
        
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
            if(sender instanceof Player){
                Player p = (Player) sender;
                PlayerData pd = PlayerData.getData(p);
                if(pd != null){
                    if(args.length == 0){
                        p.sendMessage("Current Party Members");
                        p.sendMessage("=====================");
                        if(pd.getCurrParty() != null){
                            for(Player m : pd.getCurrParty().getMembers()){
                                p.sendMessage(" - " + m.getName());
                            }
                        }else{
                            p.sendMessage("Your Party is currently empty");
                        }
                    }else{
                        if(args[0].equalsIgnoreCase("leave") && pd.getCurrParty() != null){
                            for(Player m : pd.getCurrParty().getMembers()){
                                m.sendMessage(p.getName() + " has left your party");
                            }
                            p.sendMessage("You have left the party");
                            pd.setPartyID(null);
                        }else if(args[0].equalsIgnoreCase("join") && args.length > 1){
                            PlayerData joinDat;
                            if(Bukkit.getPlayer(args[1]) != null && (joinDat = PlayerData.getData(Bukkit.getPlayer(args[1]))) != null){
                                if(joinDat.getCurrParty() != null && joinDat.getCurrParty().join(p)){
                                    p.sendMessage("Joined " + args[1] + "'s party");
                                }else{
                                    p.sendMessage("You do not have a current invite to that party!");
                                }
                            }else{
                                p.sendMessage("That player is not online!");
                            }
                        }else if(args[0].equalsIgnoreCase("invite") && args.length > 1){
                            Player r;
                            if((r = Bukkit.getPlayer(args[1])) != null){
                                if(pd.getCurrParty() == null){
                                    Parties.put(p.getName(), new Party(r, p));
                                    pd.setPartyID(p.getName());
                                }else{
                                    pd.getCurrParty().getInvites().put(r, new Date());
                                }
                                r.sendMessage(p.getName() + " has invited you to a party");
                                r.sendMessage("Type /party join " + p.getName());
                            }
                        }else if(args[0].equalsIgnoreCase("kick") && args.length > 1){
                            Player r;
                            if((r = Bukkit.getPlayer(args[1])) != null && pd.getCurrParty() != null 
                                                                       && pd.getCurrParty().getOwner().equals(p)){
                                pd.getCurrParty().getMembers().remove(r);
                            }
                        }
                    }
                }else{
                    p.sendMessage("[Parties] No Player Data found...");
                }
            }else{
                sender.sendMessage("[Parties] Only players can be in parties");
            }
            return true;
        }
    }
}
