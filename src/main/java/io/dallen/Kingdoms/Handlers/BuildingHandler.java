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
package io.dallen.Kingdoms.Handlers;

import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Blueprint;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.BuildersHut;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.NPCs.Traits.Builder;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.DBmanager;
import io.dallen.Kingdoms.Util.HotbarMenu;
import io.dallen.Kingdoms.Util.NBTmanager;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class BuildingHandler implements Listener{
    
    @Getter
    private static BuildChestOptions BuildChestHandler;
    
    @Getter
    private static BuildHotbarOptions BuildHotbarHandler;
    
    @Getter
    private static HashMap<String, StringInput> openInputs = new HashMap<String, StringInput>();
    
    @Getter
    private static HashMap<String, BuildFrame> openBuilds = new HashMap<String, BuildFrame>();
    
    private static HotbarMenu RotateMenu;
            
    static {
        BuildChestHandler = new BuildChestOptions();
        BuildHotbarHandler = new BuildHotbarOptions();
        RotateMenu = new HotbarMenu("RotateBuildMenu", BuildHotbarHandler){{
            setOption(5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
            setOption(6, new ItemStack(Material.ENCHANTED_BOOK), "Rotate Clockwise");
            setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Rotate Counter Clockwise");
        }};
    }
    
    public static class BuildChestOptions implements ChestGUI.OptionClickEventHandler{
    
        @Override
        public void onOptionClick(ChestGUI.OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("Build Menu")){
                Structure s = (Structure) e.getMenuData();
                if(e.getName().equalsIgnoreCase("Other")){
                    e.getPlayer().sendMessage("To start a building contruction type the schematic name and tick in chat");
                    Plot p = (Plot) s;
                    StringInput in = new StringInput("buildConst", p);
                    openInputs.put(e.getPlayer().getName(), in);
                }
            }
        }
    }
    
    public static class BuildHotbarOptions implements HotbarMenu.OptionClickEventHandler{
        
        @Override
        public void onOptionClick(HotbarMenu.OptionClickEvent e){
            if(e.getName().equalsIgnoreCase("Start Build")){
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                BuildersHut BuildHut = (BuildersHut) p.getMunicipal().getStructures().get(BuildersHut.class).get(0);
                Location startCorner = new Location(p.getCenter().getWorld(), p.getCenter().getX() - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                    p.getCenter().getBlockY(), p.getCenter().getBlockZ() - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                for(int y = 0; y < building.getHigh(); y++){
                    for(int z = 0; z < building.getLen(); z++){
                        for(int x = 0; x < building.getWid(); x++){
                            Location nLoc = startCorner.clone().add(x,y,z);
                            if(!nLoc.getBlock().getType().equals(Material.AIR)){
                                nLoc.getBlock().setType(Material.AIR, false);
                            }
                        }
                    }
                }
                BuildTask buildTask = new BuildTask(building, startCorner, openBuilds.get(e.getPlayer().getName()).getSpeed(), BuildHut);
                openBuilds.remove(e.getPlayer().getName());
            }else if(e.getName().equalsIgnoreCase("Rotate Clockwise")){
                openBuilds.get(e.getPlayer().getName()).getBlueprint().Rotate(90);
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = (Plot) openInputs.get(e.getPlayer().getName()).getData();
                Location startCorner = new Location(p.getCenter().getWorld(), p.getCenter().getX() - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), p.getCenter().getBlockZ() - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                for(int y = 0; y < building.getHigh(); y++){
                    for(int z = 0; z < building.getLen(); z++){
                        for(int x = 0; x < building.getWid(); x++){
                            Location nLoc = startCorner.clone().add(x,y,z);
                            Material covMat = building.getBlocks()[x][y][z].getBlock();
                            if(covMat.name().contains("STAIRS")){
                                covMat = Material.QUARTZ_STAIRS;
                            }else if(!covMat.equals(Material.AIR)){
                                covMat = Material.QUARTZ_BLOCK;
                            }
                            nLoc.getBlock().setType(covMat, false);
                            nLoc.getBlock().setData(building.getBlocks()[x][y][z].getData(), false);
                        }
                    }
                }
            }else if(e.getName().equalsIgnoreCase("Rotate Counter Clockwise")){
                openBuilds.get(e.getPlayer().getName()).getBlueprint().Rotate(-90);
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = (Plot) openInputs.get(e.getPlayer().getName()).getData();
                Location startCorner = new Location(p.getCenter().getWorld(), p.getCenter().getX() - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), p.getCenter().getBlockZ() - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                for(int y = 0; y < building.getHigh(); y++){
                    for(int z = 0; z < building.getLen(); z++){
                        for(int x = 0; x < building.getWid(); x++){
                            Location nLoc = startCorner.clone().add(x,y,z);
                            Material covMat = building.getBlocks()[x][y][z].getBlock();
                            if(covMat.name().contains("STAIRS")){
                                covMat = Material.QUARTZ_STAIRS;
                            }else if(!covMat.equals(Material.AIR)){
                                covMat = Material.QUARTZ_BLOCK;
                            }
                            nLoc.getBlock().setType(covMat, false);
                            nLoc.getBlock().setData(building.getBlocks()[x][y][z].getData(), false);
                        }
                    }
                }
            }
        }
    }
    
    @AllArgsConstructor
    public static class StringInput{
        @Getter
        private String name;
        @Getter
        private Object data;
    }
    
    @AllArgsConstructor
    public static class BuildFrame{
        @Getter
        private Blueprint Blueprint;
        @Getter
        private Plot plot;
        @Getter
        private int speed;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent ev){
        if(openInputs.containsKey(ev.getPlayer().getName())){
            ev.setCancelled(true);
            if(openInputs.get(ev.getPlayer().getName()).getName().equalsIgnoreCase("buildConst")){
                final AsyncPlayerChatEvent e = ev;
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
                   @Override
                   public void run(){
                       try {
                            String[] args = e.getMessage().split(" ");
                            Blueprint building = NBTmanager.loadData(new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + args[0] + ".schematic"));
                            Plot p = (Plot) openInputs.get(e.getPlayer().getName()).getData();
                            Location startCorner = new Location(p.getCenter().getWorld(), p.getCenter().getX() - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                                    p.getCenter().getBlockY(), p.getCenter().getBlockZ() - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                            for(int y = 0; y < building.getHigh(); y++){
                                for(int z = 0; z < building.getLen(); z++){
                                    for(int x = 0; x < building.getWid(); x++){
                                        Location nLoc = startCorner.clone().add(x,y,z);
                                        Material covMat = building.getBlocks()[x][y][z].getBlock();
                                        if(covMat.name().contains("STAIRS")){
                                            covMat = Material.QUARTZ_STAIRS;
                                        }else if(!covMat.equals(Material.AIR)){
                                            covMat = Material.QUARTZ_BLOCK;
                                        }
                                        nLoc.getBlock().setType(covMat, false);
                                        nLoc.getBlock().setData(building.getBlocks()[x][y][z].getData(), false);
                                    }
                                }
                            }
                            
                            RotateMenu.sendMenu(e.getPlayer());
                            BuildFrame frame = new BuildFrame(building, p, Integer.parseInt(args[1]));
                            openBuilds.put(e.getPlayer().getName(), frame);
                            openInputs.remove(e.getPlayer().getName());
                        } catch (IOException ex) {
                            Logger.getLogger(MultiBlockHandler.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (DataFormatException ex) {
                            Logger.getLogger(MultiBlockHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                   } 
                });
            }
        }
    }
    
    public static class BuildTask implements Runnable{
        
        private int x = 0;
        
        private int y = 0; 
        
        private int z = 0;
        
        private Blueprint Building;
        
        private Location startCorner;
        
        private BuildersHut BuildHut;
        
        private NPC Builder;
        
        private boolean running = true;
        
        private int step = 64;
        
        public BuildTask(Blueprint building, Location start, int speed, BuildersHut BuildHut){
            Builder = Main.getNPCs().spawnBuilder("BingRazor", BuildHut.getCenter());
            Builder.getNavigator().setTarget(start);
            this.Building = building;
            this.startCorner = start;
            this.BuildHut = BuildHut;
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), this, speed, speed);
        }
        
        @Override
        public void run(){
            if(running && !Builder.getNavigator().isNavigating()){
                if(step == 0){
                    Builder.getTrait(Builder.class).getSupplies(startCorner);
                    step = 64;
                }
                step--;
//                LogUtil.printDebug(x + ", " + y + ", " + z);
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
