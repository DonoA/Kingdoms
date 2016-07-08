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
package io.dallen.kingdoms.rpg.Contract;

import io.dallen.Kingdoms.RPG.Contract.Types.BuildContract;
import io.dallen.kingdoms.core.Handlers.MultiBlockHandler;
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Structures.Types.BuildersHut;
import io.dallen.kingdoms.core.Overrides.KingdomMaterial;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Storage;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.core.Structures.Vaults.BuildingVault;
import io.dallen.kingdoms.utilities.Blueprint;
import io.dallen.kingdoms.utilities.Blueprint.BlueBlock;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import io.dallen.kingdoms.utilities.Utils.HotbarMenu;
import io.dallen.kingdoms.utilities.Utils.LocationUtil;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import io.dallen.kingdoms.utilities.Utils.NBTmanager;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            setOption(4, KingdomMaterial.START_BUILD.getItemStack(), "Build");
            setOption(5, KingdomMaterial.ARROW_CLOCKWISE.getItemStack(), "Rotate Clockwise");
            setOption(3, KingdomMaterial.ARROW_COUNTER_CLOCKWISE.getItemStack(), "Rotate Counter Clockwise");
            
            setOption(2, KingdomMaterial.ARROW_RIGHT.getItemStack(), "Shift East");
            setOption(6, KingdomMaterial.ARROW_LEFT.getItemStack(), "Shift West");
            
            setOption(1, KingdomMaterial.ARROW_UP.getItemStack(), "Shift North");
            setOption(7, KingdomMaterial.ARROW_DOWN.getItemStack(), "Shift South");
        }};
    }
    
    public static class BuildChestOptions{
    
        public void onOptionClick(ChestGUI.OptionClickEvent e, Structure struct){
            Structure s = struct;
            if(e.getName().equalsIgnoreCase("Other")){
                e.getPlayer().sendMessage("To start a building contruction type the schematic name and tick in chat");
                Plot p = (Plot) s;
                StringInput in = new StringInput("buildConst", p, (BuildContract) e.getMenuData());
                openInputs.put(e.getPlayer().getName(), in);
            }else{
                try {
                    String[] args = new String[]{e.getName().replace(".schematic", ""), "5"};
                    Blueprint building = NBTmanager.loadData(new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + args[0] + ".schematic"));
                    Plot p = (Plot) openInputs.get(e.getPlayer().getName()).getData();
                    if(p.getLength() + 1 <= building.getLen() || p.getWidth() + 1 <= building.getWid()){
                        building.Rotate(90);
                    }
                    if(p.getLength() + 1 <= building.getLen() || p.getWidth() + 1 <= building.getWid()){
                        e.getPlayer().sendMessage("That structure is too large for this plot");
                        return;
                    }
                    List<ItemStack> mats = new ArrayList<ItemStack>();
                    for(Blueprint.BlueBlock[][] bbarr1 : building.getBlocks()){
                        for(Blueprint.BlueBlock[] bbarr2: bbarr1){
                            for(Blueprint.BlueBlock bb : bbarr2){
                                ItemStack is = new ItemStack(bb.getBlock());
                                is.getData().setData(bb.getData());
                                boolean found = false;
                                for(ItemStack mat : mats){
                                    if(mat.isSimilar(is)){
                                        mat.setAmount(mat.getAmount()+1);
                                        found = true;
                                    }
                                }
                                if(!found){
                                    mats.add(is);
                                }
                            }
                        }
                    }
                    List<Structure> locs = new ArrayList<Structure>();
                    for(ItemStack is : mats){
                        locs = p.getMunicipal().materialLocation(is);
                        for(Structure st : locs){
                            is.setAmount(is.getAmount()-((BuildingVault)((Storage) st).getStorage()).getMaterial(is).getAmount());
                        }
                        if(is.getAmount() > 0){
                            e.getPlayer().sendMessage("Your municipality does not have the materials to build this structure");
                            return;
                        }
                    }
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
                    BuildFrame frame = new BuildFrame(building, p, Integer.parseInt(args[1]), openInputs.get(e.getPlayer().getName()).getContract());
                    openBuilds.put(e.getPlayer().getName(), frame);
                    openInputs.remove(e.getPlayer().getName());
                } catch (IOException | DataFormatException ex) {
                    Logger.getLogger(MultiBlockHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static class BuildHotbarOptions implements HotbarMenu.OptionClickEventHandler{
        
        @Override
        public void onOptionClick(HotbarMenu.OptionClickEvent e){
//            LogUtil.printDebug("Hotbar event called");
            if(e.getName().equalsIgnoreCase("Build")){
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                BuildersHut BuildHut = (BuildersHut) p.getMunicipal().getStructures().get(BuildersHut.class).get(0);
                Point offSet = building.getOffSet();
                Location startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.CLEAR);
                List<ItemStack> mats = new ArrayList<ItemStack>();
                for(BlueBlock[][] bbarr1 : building.getBlocks()){
                    for(BlueBlock[] bbarr2: bbarr1){
                        for(BlueBlock bb : bbarr2){
                            ItemStack is = new ItemStack(bb.getBlock());
                            is.getData().setData(bb.getData());
                            boolean found = false;
                            for(ItemStack mat : mats){
                                if(mat.isSimilar(is)){
                                    mat.setAmount(mat.getAmount()+1);
                                    found = true;
                                }
                            }
                            if(!found){
                                mats.add(is);
                            }
                        }
                    }
                }
                List<Structure> locs = new ArrayList<Structure>();
                for(ItemStack is : mats){
                    locs = p.getMunicipal().materialLocation(is);
                    for(Structure s : locs){
                        is.setAmount(((BuildingVault)((Storage) s).getStorage()).removeItem(is));
                    }
                    if(is.getAmount() > 0){
                        e.getPlayer().sendMessage("Your municipality does not have the materials to build this structure");
                        return;
                    }
                }
                openBuilds.get(e.getPlayer().getName()).getContract().finishSelectBuilding(building);
                openBuilds.remove(e.getPlayer().getName());
            }else if(e.getName().equalsIgnoreCase("Rotate Clockwise")){
                e.setClose(false);
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                Point offSet = building.getOffSet();
                Location startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.CLEAR);
                building.Rotate(90);
                if(p.getLength() + 1 <= building.getLen() || p.getWidth() + 1  <= building.getWid()){
                    building.Rotate(90);
                }
                startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.FRAME);
            }else if(e.getName().equalsIgnoreCase("Rotate Counter Clockwise")){
                e.setClose(false);
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                Point offSet = building.getOffSet();
                Location startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.CLEAR);
                building.Rotate(-90);
                if(p.getLength() + 1 <= building.getLen() || p.getWidth() + 1 <= building.getWid()){
                    building.Rotate(-90);
                }
                startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.FRAME);
            }else if(e.getName().equalsIgnoreCase("Shift East")){
                e.setClose(false);
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                Point offSet = building.getOffSet();
                Location startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                if((p.getWidth() - building.getWid())/2 + 1 < offSet.x + 1){
                    return;
                }
                building.build(startCorner, Blueprint.buildType.CLEAR);
                offSet.x += 1;
                // ROTATE
                startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.FRAME);
            }else if(e.getName().equalsIgnoreCase("Shift West")){
                e.setClose(false);
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                Point offSet = building.getOffSet();
                Location startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                if((p.getWidth() - building.getWid())/2 < Math.abs(offSet.x - 1)){
                    return;
                }
                building.build(startCorner, Blueprint.buildType.CLEAR);
                offSet.x -= 1;
                // ROTATE
                startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.FRAME);
            }else if(e.getName().equalsIgnoreCase("Shift North")){
                e.setClose(false);
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                Point offSet = building.getOffSet();
                Location startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                if((p.getLength() - building.getLen())/2 < Math.abs(offSet.y - 1)){
                    return;
                }
                building.build(startCorner, Blueprint.buildType.CLEAR);
                offSet.y -= 1;
                // ROTATE
                startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.FRAME);
            }else if(e.getName().equalsIgnoreCase("Shift South")){
                e.setClose(false);
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                Point offSet = building.getOffSet();
                Location startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                if((p.getLength() - building.getLen())/2 + 1 < offSet.y + 1){
                    return;
                }
                building.build(startCorner, Blueprint.buildType.CLEAR);
                offSet.y += 1;
                // ROTATE
                startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.FRAME);
            }else if(e.getName().equalsIgnoreCase("Cancel")){
                Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
                Point offSet = building.getOffSet();
                Location startCorner = new Location(p.getCenter().getWorld(), (p.getCenter().getX() + offSet.x) - building.getWid()/2  + (building.getWid() % 2 == 0 ? 1 : 0),
                        p.getCenter().getBlockY(), (p.getCenter().getBlockZ() + offSet.y) - building.getLen()/2 + (building.getLen() % 2 == 0 ? 1 : 0));
                building.build(startCorner, Blueprint.buildType.CLEAR);
                openBuilds.remove(e.getPlayer().getName());
            }
        }
    }
    
    
    
    @AllArgsConstructor
    public static class StringInput{
        @Getter
        private String name;
        @Getter
        private Object data;
        @Getter
        private BuildContract contract;
    }
    
    @AllArgsConstructor
    public static class BuildFrame{
        @Getter
        private Blueprint Blueprint;
        @Getter
        private Plot plot;
        @Getter
        private int speed;
        @Getter
        private BuildContract contract;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent ev){
        if(openInputs.containsKey(ev.getPlayer().getName())){
            ev.setCancelled(true);
            if(openInputs.get(ev.getPlayer().getName()).getName().equalsIgnoreCase("buildConst")){
                if(!new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "multiblock" + DBmanager.getFileSep() + ev.getMessage().split(" ")[0] + ".schematic").exists()){
                    ev.getPlayer().sendMessage("That schemaic cannot be found!");
                    ev.getPlayer().sendMessage("Exiting build setup!");
                    openInputs.remove(ev.getPlayer().getName());
                    return;
                }
                final AsyncPlayerChatEvent e = ev;
                Bukkit.getScheduler().runTask(KingdomsCore.getPlugin(), new Runnable(){
                    @Override
                    public void run(){
                        try {
                            String[] args = e.getMessage().split(" ");
                            Blueprint building = NBTmanager.loadData(new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + args[0] + ".schematic"));
                            Plot p = (Plot) openInputs.get(e.getPlayer().getName()).getData();
                            if(p.getLength() + 1 <= building.getLen() || p.getWidth() + 1 <= building.getWid()){
                                building.Rotate(90);
                            }
                            if(p.getLength() + 1 <= building.getLen() || p.getWidth() + 1 <= building.getWid()){
                                e.getPlayer().sendMessage("That structure is too large for this plot");
                                return;
                            }
                            List<ItemStack> mats = new ArrayList<ItemStack>();
                            for(BlueBlock[][] bbarr1 : building.getBlocks()){
                                for(BlueBlock[] bbarr2: bbarr1){
                                    for(BlueBlock bb : bbarr2){
                                        ItemStack is = new ItemStack(bb.getBlock());
                                        is.getData().setData(bb.getData());
                                        boolean found = false;
                                        for(ItemStack mat : mats){
                                            if(mat.isSimilar(is)){
                                                mat.setAmount(mat.getAmount()+1);
                                                found = true;
                                            }
                                        }
                                        if(!found){
                                            mats.add(is);
                                        }
                                    }
                                }
                            }
                            List<Structure> locs = new ArrayList<Structure>();
                            for(ItemStack is : mats){
                                locs = p.getMunicipal().materialLocation(is);
                                for(Structure s : locs){
                                    is.setAmount(is.getAmount()-((BuildingVault)((Storage) s).getStorage()).getMaterial(is).getAmount());
                                }
                                if(is.getAmount() > 0){
                                    e.getPlayer().sendMessage("Your municipality does not have the materials to build this structure");
                                    return;
                                }
                            }
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
                            BuildFrame frame = new BuildFrame(building, p, Integer.parseInt(args[1]), openInputs.get(e.getPlayer().getName()).getContract());
                            openBuilds.put(e.getPlayer().getName(), frame);
                            openInputs.remove(e.getPlayer().getName());
                        } catch (IOException | DataFormatException ex) {
                            Logger.getLogger(MultiBlockHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } 
                });
            }
        }
    }
    
    public static class BuildTask implements Runnable{
        
        private ArrayList<BlueBlock> blocks = new ArrayList<BlueBlock>();
        
        private ArrayList<Location> blockLocations = new ArrayList<Location>();
        
        private Location startCorner;
        
        private BuildersHut BuildHut;
        
        private NPC Builder;
        
        private boolean running = true;
        
        private int step = 0;
        
        public BuildTask(Blueprint building, Location start, int speed, BuildersHut BuildHut){
            LogUtil.printDebug(LocationUtil.asPoint(BuildHut.getCenter()));
            Builder = KingdomsCore.getNPCs().spawnBuilder("BingRazer", BuildHut.getCenter());
            Builder.getNavigator().setTarget(start);
            for(int y = 0; y < building.getHigh(); y++){
                for(int z = 0; z < building.getLen(); z++){
                    for(int x = 0; x < building.getWid(); x++){
                        if(!building.getBlocks()[x][y][z].getBlock().equals(Material.AIR)){
                            blockLocations.add(start.clone().add(x,y,z));
                            blocks.add(building.getBlocks()[x][y][z]);
                        }
                    }
                }
            }
            this.startCorner = start;
            this.BuildHut = BuildHut;
            Bukkit.getScheduler().scheduleSyncRepeatingTask(KingdomsCore.getPlugin(), this, speed, speed);
        }
        
        @Override
        public void run(){
            if(!Builder.getNavigator().isNavigating()){
                if(running){
                    if(step < blocks.size()){
                        Builder.teleport(blockLocations.get(step).clone().add(0, 1, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                        blockLocations.get(step).getBlock().setType(blocks.get(step).getBlock(), false);
                        blockLocations.get(step).getBlock().setData(blocks.get(step).getData(), false);
                        step++;
                    }else{
                        if(Builder.isSpawned())
                            Builder.getNavigator().setTarget(BuildHut.getCenter());
                        running = false;
                    }
                }else{
                    if(Builder.isSpawned())
                        Builder.despawn();
                }
            }
        }
    }
}
