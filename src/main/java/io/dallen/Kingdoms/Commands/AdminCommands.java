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
import io.dallen.Kingdoms.Main;
import java.lang.reflect.InvocationTargetException;
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
        
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(cmd.getName().equalsIgnoreCase("strack")){
            
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
    }
}
