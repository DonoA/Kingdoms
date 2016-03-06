/*
 * This file is part of Kingdom.
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
package io.dallen.kingdom.Handlers;

import io.dallen.kingdom.Faction;
import io.dallen.kingdom.PlayerData;
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
        e.setFormat(ChatColor.WHITE + "[%c]" + PlayerData.getPlayerDat().get(e.getPlayer()).getRole().getTitle() + "%s" + ChatColor.WHITE + ": %s");
        switch(PlayerChatModes.get(e.getPlayer())){
            case 0: //Public
                e.setFormat(e.getFormat().replace("%c", "Public"));
               break;
            case 1: //Faction
                Faction f = Faction.getFactions().get(PlayerData.getPlayerDat().get(e.getPlayer()).getFaction());
                e.setFormat(e.getFormat().replace("%c", f.getName()));
                e.getRecipients().clear();
                e.getRecipients().addAll(f.getOnlinePlayers());
                break;
            case 2: //Party
                e.setFormat(e.getFormat().replace("%c", "Party"));
                e.getRecipients().clear();
                e.getRecipients().addAll(PlayerData.getPlayerDat().get(e.getPlayer()).getCurrParty().getMembers());
                break;
        }   
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        return true;
    }
}
