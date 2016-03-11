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

import java.util.HashMap;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class ModerationCommands implements CommandExecutor{
            
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(cmd.getName().equalsIgnoreCase("where")){
            
        }else if(cmd.getName().equalsIgnoreCase("ban")){
            
        }else if(cmd.getName().equalsIgnoreCase("unban")){
            
        }else if(cmd.getName().equalsIgnoreCase("ipban")){
            
        }else if(cmd.getName().equalsIgnoreCase("unipban")){
            
        }else if(cmd.getName().equalsIgnoreCase("kick")){
            
        }else if(cmd.getName().equalsIgnoreCase("tmpban")){
            
        }else if(cmd.getName().equalsIgnoreCase("uuid")){
            
        }else if(cmd.getName().equalsIgnoreCase("fjail")){
            
        }else if(cmd.getName().equalsIgnoreCase("broadcast")){
            
        }else{
            if(sender instanceof Player){
                Player p = (Player) sender;
                if(cmd.getName().equalsIgnoreCase("invspy")){
                    
                }else if(cmd.getName().equalsIgnoreCase("vanish")){
                    
                }
            }
        }
        return false;
    }
}
