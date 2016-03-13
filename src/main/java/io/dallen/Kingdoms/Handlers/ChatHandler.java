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

import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.PlayerData;
import io.dallen.Kingdoms.Util.MuteCommand;
import io.dallen.Kingdoms.Util.TimeUtil;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author donoa_000
 */
public class ChatHandler implements Listener, CommandExecutor{
    
    @Getter @Setter//      player, chat mode
    private static HashMap<Player, Integer> PlayerChatModes = new HashMap<Player, Integer>();
    
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        PlayerData pd = PlayerData.getData(e.getPlayer());
        if(pd.getMuted() == null){
            e.setFormat(ChatColor.WHITE + "[%c]" + PlayerData.getPlayerDat().get(e.getPlayer()).getRole().getTitle() + "%s" + ChatColor.WHITE + ": %s");
            switch(PlayerChatModes.get(e.getPlayer())){
                case 0: //Public
                    e.setFormat(e.getFormat().replace("%c", "Public"));
                    if(MuteCommand.getLocalMutes().containsKey(e.getPlayer())){
                        e.getRecipients().removeAll(MuteCommand.getLocalMutes().get(e.getPlayer()));
                    }
                   break;
                case 1: //Faction
                    Kingdom f = Kingdom.getFactions().get(PlayerData.getPlayerDat().get(e.getPlayer()).getFaction());
                    e.setFormat(e.getFormat().replace("%c", f.getName()));
                    e.getRecipients().clear();
                    e.getRecipients().addAll(f.getOnlinePlayers());
                    if(MuteCommand.getLocalMutes().containsKey(e.getPlayer())){
                        e.getRecipients().removeAll(MuteCommand.getLocalMutes().get(e.getPlayer()));
                    }
                    break;
                case 2: //Party
                    e.setFormat(e.getFormat().replace("%c", "Party"));
                    e.getRecipients().clear();
                    e.getRecipients().addAll(PlayerData.getPlayerDat().get(e.getPlayer()).getCurrParty().getMembers());
                    if(MuteCommand.getLocalMutes().containsKey(e.getPlayer())){
                        e.getRecipients().removeAll(MuteCommand.getLocalMutes().get(e.getPlayer()));
                    }
                    break;
            }
        }else{
            e.getPlayer().sendMessage("You are currently muted, you cannot chat");
            if(pd.getMuted().getTime() != null){
                e.getPlayer().sendMessage("You are muted for another " + TimeUtil.asTime(new Date(System.currentTimeMillis()), pd.getMuted().getTime()));
            }
            e.getRecipients().clear();
            e.setCancelled(true);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(args.length >= 1){
                switch(args[0]){
                    case "global":
                        PlayerChatModes.put(p, 0);
                        break;
                    case "g":
                        PlayerChatModes.put(p, 0);
                        break;
                    case "kingdom":
                        PlayerChatModes.put(p, 1);
                        break;
                    case "k":
                        PlayerChatModes.put(p, 1);
                        break;
                    case "party":
                        PlayerChatModes.put(p, 2);
                        break;
                    case "p":
                        PlayerChatModes.put(p, 2);
                        break;
                }
            }else{
                if(PlayerChatModes.get(p) == 2){
                    PlayerChatModes.put(p, 0);
                }else{
                    PlayerChatModes.put(p, PlayerChatModes.get(p)+1);
                }
            }
            switch(PlayerChatModes.get(p)){
                case 0:
                    p.sendMessage("[ChatHandler] Switched to global chat");
                    break;
                case 1:
                    p.sendMessage("[ChatHandler] Switched to kingdom chat");
                    break;
                case 2:
                    p.sendMessage("[ChatHandler] Switched to party chat");
                    if(PlayerData.getPlayerDat().get(p).getCurrParty() == null){
                        p.sendMessage("[ChatHandler] Your party is currently empty!");
                    }
                    break;
            }
            return true;
        }else{
            sender.sendMessage(ChatColor.RED + "[ChatHandler] Console chat modes is not allowed!");
            return true;
        }
    }
}
