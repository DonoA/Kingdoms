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
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                Location startCorner = new Location(p.getCenter().getWorld(), p.getCenter().getX() - building.getWid()/2, 
                        p.getCenter().getBlockY(), p.getCenter().getBlockZ() - building.getLen()/2);
                for(int y = 0; y < building.getHigh(); y++){
                    for(int z = 0; z < building.getLen(); z++){
                        for(int x = 0; x < building.getWid(); x++){
                            Location nLoc = startCorner.clone().add(x,y,z);
                            nLoc.getBlock().setType(building.getBlocks()[x][y][z].getBlock(), false);
                            nLoc.getBlock().setData(building.getBlocks()[x][y][z].getData(), false);
                            
                        }
                }
        }
            } catch (IOException | DataFormatException ex) {
                Logger.getLogger(DebugCommands.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
    
}
