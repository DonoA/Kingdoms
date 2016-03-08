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
package io.dallen.Kingdoms;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class KingdomCommands implements CommandExecutor{
    
    private static NPC npc;
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        Player p = (Player) sender;
        if(args[0].equalsIgnoreCase("spawn")){
            npc = Main.getNPCs().getNPCreg().createNPC(EntityType.PLAYER, "Dallen");
            npc.spawn(Bukkit.getWorlds().get(0).getSpawnLocation());
        }else if(args[0].equalsIgnoreCase("target")){
            npc.getNavigator().setTarget(p.getLocation());
        }
        return true;
    }
}
