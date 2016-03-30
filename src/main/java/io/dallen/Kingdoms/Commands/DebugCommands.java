/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of Kingdoms for the Morphics Network.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
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

import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Blueprint;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.DBmanager;
import io.dallen.Kingdoms.Util.NBTmanager;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author Donovan Allen
 */
public class DebugCommands implements CommandExecutor{
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("fillplot")){
            try {
                Blueprint building = NBTmanager.loadData(new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + args[0] + ".schematic"));
                Plot p = Plot.inPlot(((Player) sender).getLocation());
                Location startCorner = new Location(p.getCenter().getWorld(), p.getCenter().getX() - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0), 
                        p.getCenter().getBlockY(), p.getCenter().getBlockZ() - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new buildTask(building, startCorner), Integer.parseInt(args[1]), Integer.parseInt(args[1]));
            } catch (IOException | DataFormatException ex) {
                Logger.getLogger(DebugCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else if(cmd.getName().equalsIgnoreCase("setskins")){
            for(WrappedSignedProperty p : Main.getSkinHandler().getProperties()){
                LogUtil.printDebug(p.getName());
                LogUtil.printDebug(p.getValue());
            }
            Main.getSkinHandler().setSkin(args[0]);
            Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), Main.getSkinHandler());
        }
        return true;
    }
    
    public static class buildTask implements Runnable{
        
        private int x = 0;
        
        private int y = 0; 
        
        private int z = 0;
        
        private Blueprint Building;
        
        private Location startCorner;
        
        private NPC Builder;
        
        private boolean running = true;
        
        public buildTask(Blueprint building, Location start){
            Builder = Main.getNPCs().getNPCreg().createNPC(EntityType.PLAYER, "BingRazer");
            Builder.spawn(start);
            this.Building = building;
            this.startCorner = start;
        }
        
        @Override
        public void run(){
            if(running){
                LogUtil.printDebug(x + ", " + y + ", " + z);
                if(x < Building.getWid() - (Building.getWid() % 2 == 0 ? 1 : 0)){
                    Location nLoc = startCorner.clone().add(x,y,z);
                    nLoc.getBlock().setType(Building.getBlocks()[x][y][z].getBlock(), false);
                    nLoc.getBlock().setData(Building.getBlocks()[x][y][z].getData(), false);
                    Builder.teleport(nLoc.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    x++;
                }else{
                    x = 0;
                    if(z < Building.getLen() - 1){ // this is a bit strange, it seems to work tho
                        Location nLoc = startCorner.clone().add(x,y,z);
                        nLoc.getBlock().setType(Building.getBlocks()[x][y][z].getBlock(), false);
                        nLoc.getBlock().setData(Building.getBlocks()[x][y][z].getData(), false);
                        Builder.teleport(nLoc.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        z++;
                    }else{
                        z = 0;
                        if(y < Building.getHigh() - (Building.getHigh() % 2 == 0 ? 1 : 0)){
                            Location nLoc = startCorner.clone().add(x,y,z);
                            nLoc.getBlock().setType(Building.getBlocks()[x][y][z].getBlock(), false);
                            nLoc.getBlock().setData(Building.getBlocks()[x][y][z].getData(), false);
                            Builder.teleport(nLoc.add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            y++;
                        }else{
                            running = false;
                        }
                    }
                }
            }
        }
    }
}
