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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import io.dallen.Kingdoms.Faction;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.PermissionManager;
import java.lang.reflect.InvocationTargetException;
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
public class AdminCommands implements CommandExecutor{
    
    @Getter
    private int newUsers = 0;
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(sender.hasPermission(PermissionManager.getOwnerPermission())){
            if(cmd.getName().equalsIgnoreCase("strack") && args.length > 0){
                if(args[0].equalsIgnoreCase("load")){
                    
                }else if(args[0].equalsIgnoreCase("mem")){
                    long used = (Main.getRuntime().totalMemory() - Main.getRuntime().freeMemory())/Main.getRuntime().totalMemory();
                }else if(args[0].equalsIgnoreCase("users")){
                    
                }else if(args[0].equalsIgnoreCase("dbSize")){
                    
                }else if(args[0].equalsIgnoreCase("newUsers")){
                    
                }else if(args[0].equalsIgnoreCase("kingdoms")){
                    sender.sendMessage("Current Kingdoms:");
                    sender.sendMessage("======================");
                    for(String f : Faction.getFactions().keySet()){
                        sender.sendMessage(" - " + f);
                    }
                }else if(args[0].equalsIgnoreCase("covens")){
                    
                }else if(args[0].equalsIgnoreCase("roles")){
                    
                }else if(args[0].equalsIgnoreCase("levels")){
                    
                }
            }else if(cmd.getName().equalsIgnoreCase("crash") && args.length > 0){
                if(Bukkit.getPlayer(args[0]) != null){
                    Player p = Bukkit.getPlayer(args[0]);
                    PacketContainer CrashPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.RESPAWN, false);

                    CrashPacket.getIntegers().write(0, 0);
                    CrashPacket.getBytes().write(1, (byte) 0).write(2, (byte) 0);
                    CrashPacket.getStrings().write(3, "default");
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(p, CrashPacket);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }else{
                    sender.sendMessage("Player not found!");
                }
            }
            return false;
        }else{
            sender.sendMessage("Not allowed!");
            return true;
        }
    }
}
