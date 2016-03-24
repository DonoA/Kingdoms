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

import io.dallen.Kingdoms.Main;
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
