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
package io.dallen.Kingdoms.Handlers;

import com.google.common.primitives.Ints;
import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Blueprint;
import io.dallen.Kingdoms.Kingdom.Structures.Contract;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.BuildersHut;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.Wall;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.WallType;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import io.dallen.Kingdoms.Util.DBmanager;
import io.dallen.Kingdoms.Util.ItemUtil;
import io.dallen.Kingdoms.Util.LocationUtil;
import io.dallen.Kingdoms.Util.NBTmanager;
import java.awt.Point;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class MultiBlockHandler implements Listener{
    
    private ChestGUI NewPlotMenu;
    
    private ChestGUI SetPlotType;
    
    private ChestGUI ViewPlotMenu;
    
    private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
    
    @Getter
    private static MBOptions optionHandler;
    
    @Getter
    private static HashMap<String, StringInput> openInputs = new HashMap<String, StringInput>();
    
    @Getter
    private static HashMap<String, BuildFrame> openBuilds = new HashMap<String, BuildFrame>();
    
    public MultiBlockHandler(){
        optionHandler = new MBOptions();
        NewPlotMenu = new ChestGUI("New Plot", InventoryType.HOPPER, optionHandler) {{
            setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", "");
            setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Cancel Plot Claim", "");
        }};
        
        SetPlotType = new ChestGUI("Set Plot Type", 4, optionHandler) {{
            setOption(9*0 + 1, new ItemStack(Material.ENCHANTED_BOOK), "Storeroom", "");
            setOption(9*0 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Barracks", "");
            setOption(9*0 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Training Ground", "");
            setOption(9*0 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Town Hall", "");
            setOption(9*0 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Blacksmith", "");
            setOption(9*0 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Farm", "");
            setOption(9*1 + 1, new ItemStack(Material.ENCHANTED_BOOK), "Builder's Hut", "");
            setOption(9*1 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Bank", "");
            setOption(9*1 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Stable", "");
            setOption(9*1 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Dungeon", "");
            setOption(9*1 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Marketplace", "");
            setOption(9*1 + 6, new ItemStack(Material.ENCHANTED_BOOK), "Court", "");
            setOption(9*2 + 2, new ItemStack(Material.ENCHANTED_BOOK), "Wall", "");
            setOption(9*2 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Wall with Door", "");
            setOption(9*2 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Corner", "");
            setOption(9*2 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Tower", "");
            setOption(9*3 + 3, new ItemStack(Material.ENCHANTED_BOOK), "Custom Contract", "");
            setOption(9*3 + 4, new ItemStack(Material.ENCHANTED_BOOK), "Demolish", "");
            setOption(9*3 + 5, new ItemStack(Material.ENCHANTED_BOOK), "Erase", "");
        }};
        
        ViewPlotMenu = new ChestGUI("Plot Info", InventoryType.HOPPER, optionHandler) {{
            setOption(2, new ItemStack(Material.ENCHANTED_BOOK), "No current contracts avalible", "");
        }};
        
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(WallSystem.Wall.getDamageBars().contains(e.getPlayer().getName())){
            final PlayerMoveEvent ev = e;
            new Thread(new Runnable(){
                @Override
                public void run(){
                    for(Plot p : Plot.getAllPlots()){
                        if(p instanceof Wall){
                            Wall w = (Wall) p;
                            if(w.getCurrInteracters().contains(ev.getPlayer()) && ev.getTo().distance(w.getCenter()) > 20){
                                w.getCurrInteracters().remove(ev.getPlayer());
                                WallSystem.Wall.getDamageBars().remove(ev.getPlayer().getName());
                                w.getDamageBar().removePlayer(ev.getPlayer());
                            }
                        }
                    }
                }
            }).start();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e){
        if((!cooldown.containsKey(e.getPlayer())) || 
              (cooldown.containsKey(e.getPlayer()) && cooldown.get(e.getPlayer()) < System.currentTimeMillis() - 500)){
               cooldown.put(e.getPlayer(), System.currentTimeMillis());
            if(e.hasBlock()){
                Plot p = Plot.inPlot(e.getClickedBlock().getLocation());
                if(p != null){
                    if(p instanceof Storage){
                        Storage s = (Storage) p;
                        e.setCancelled(s.interact(e));
                    }
                    if(p instanceof Wall && e.hasItem() && e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                        e.setCancelled(true);
                        Wall w = (Wall) p;
                        if(e.getItem().getType().name().contains("PICKAXE")){
                            if(!w.getCurrInteracters().contains(e.getPlayer())){
                                w.getCurrInteracters().add(e.getPlayer());
                                if(!WallSystem.Wall.getDamageBars().contains(e.getPlayer().getName())){
                                    WallSystem.Wall.getDamageBars().add(e.getPlayer().getName());
                                }
                                w.getDamageBar().addPlayer(e.getPlayer());
                            }
                            w.damage();
                        }else if(e.getItem().getType().equals(Material.DIAMOND_HOE)){
                            if(!w.getCurrInteracters().contains(e.getPlayer())){
                                w.getCurrInteracters().add(e.getPlayer());
                                if(!WallSystem.Wall.getDamageBars().contains(e.getPlayer().getName())){
                                    WallSystem.Wall.getDamageBars().add(e.getPlayer().getName());
                                }
                                w.getDamageBar().addPlayer(e.getPlayer());
                            }
                            w.repair();
                        }
                    }
                }
            }
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
                if(!e.hasItem()){
                    if(e.hasBlock()){
                        Block b = e.getClickedBlock();
                        if(b.getType().equals(Material.REDSTONE_TORCH_ON)){ // TESTING: REDSTONE_TORCH_ON
                            final Location l = b.getLocation();
                            final Player p = e.getPlayer();
                            p.sendMessage("Calculating Plot...");
                            new Thread(new Runnable(){
                                @Override
                                public void run(){ // Calculate redstone torch plot
                                    ArrayList<Point> corners = new ArrayList<Point>();
                                    Point start = LocationUtil.asPoint(l);
                                    corners.add(start);
                                    //Redstone direction check needs to be first
                                    boolean complete = false;
                                    boolean direction = true;//true:X, false:Z
                                    //Start with X direction
                                    //Then check Z
                                    //continue until the found point is also the start point
                                    Point last; // the last confirmed point
                                    Point current = start; // the current point being delt with
                                    while(!complete){
                                        last = current; // cycle points
                                        current = null;
                                        for(int i = 1; i<64 && current == null; i++){
                                            Location neg = new Location(l.getWorld(), (direction ? last.getX() - i : last.getX()), l.getBlockY(), (!direction ? last.getY() - i : last.getY())); // the point in the negative direction
                                            Location pos = new Location(l.getWorld(), (direction ? last.getX() + i : last.getX()), l.getBlockY(), (!direction ? last.getY() + i : last.getY())); // the point in the positive direction
                                            if(neg.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){ // Test in the negative direction
                                                current = LocationUtil.asPoint(neg);
                                                if(current.equals(start)){// If the found point is the start point
                                                    complete = true;
                                                }
                                            }
                                            if(pos.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){
                                                if(current != null){ // If a point has already been found
                                                    p.sendMessage("Plot cannot be deturmined, please use the redstone dust method");
                                                    return;
                                                }
                                                if(!complete){ // If the loop is complete
                                                    current = new Point(pos.getBlockX(), pos.getBlockZ());
                                                    if(current.equals(start)){
                                                        complete = true;
                                                    }
                                                }
                                            }
                                        }
                                        if(current == null /*|| corners.contains(last)*/){ // If the test failed to located the shape
                                            p.sendMessage("Could not calculate plot");
                                            return;
                                        }
                                        if(!complete){//dont add the start point into the list again
                                            corners.add(current);
                                        }
                                        direction = !direction;
                                    }
                                    LogUtil.printDebug(Arrays.toString(corners.toArray()));
                                    int[] Xs = new int[corners.size()];
                                    int[] Zs = new int[corners.size()];
                                    int i = 0;
                                    for(Point p : corners){
                                        Xs[i] = (int) p.getX();
                                        Zs[i] = (int) p.getY();
                                        i++;
                                    }
                                    Plot NewPlot = new Plot(new Polygon(Xs, Zs, corners.size()), LocationUtil.asLocation(LocationUtil.calcCenter(corners.toArray(new Point[1])), l.getWorld(), l.getBlockY()), p, null);
                                    for(Plot plot : Plot.getAllPlots()){
                                        if(plot.getCenter().equals(NewPlot.getCenter())){
                                            if(plot.getOwner().equals(p)){
                                                p.sendMessage("You already own this plot!");
                                            }else{
                                                p.sendMessage("This plot has already been claimed!");
                                            }
                                            return;
                                        }
                                    }
                                    if(NewPlot.isValid()){
                                        NewPlotMenu.setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", NewPlot, "Plot size: " + -1 + ", " + -1).sendMenu(p);
                                    }else{
                                        p.sendMessage("Invalid Plot");
                                    }
                                }
                            }).start();
                        }
                    }
                }else if(e.getItem().hasItemMeta()){
                    if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Multi Tool")){
                        Plot p = Plot.inPlot(e.getPlayer().getLocation());
                        if(p==null){
                            return;
                        }
                        if(p.getOwner().equals(e.getPlayer())){
                            if(p.isEmpty()){
                                SetPlotType.sendMenu(e.getPlayer());
                            }else{
                                p.sendEditMenu(e.getPlayer());
                            }
                        }else{
                            int loc = 0;
                            for(Contract ct : p.getContracts()){
                                ViewPlotMenu.setOption(loc, new ItemStack(Material.ENCHANTED_BOOK), ct.getMessage(), ct.getPay(), ct.getType().getName());
                                loc++;
                            }
                            ViewPlotMenu.sendMenu(e.getPlayer());
                        }
                    }else if(e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Start Build")){
                        Blueprint building = openBuilds.get(e.getPlayer().getName()).getBlueprint();
                        Plot p = openBuilds.get(e.getPlayer().getName()).getPlot();
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
                        BuildTask buildTask = new BuildTask(building, startCorner, openBuilds.get(e.getPlayer().getName()).getSpeed());
                        openBuilds.remove(e.getPlayer().getName());
                    }
                }
            }
        }
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
                            Inventory hotbar =  Bukkit.createInventory(e.getPlayer(), 9);
                            e.getPlayer().getInventory().setItemInMainHand(ItemUtil.setItemNameAndLore(Material.WATCH, "Start Build"));
        //                    e.getPlayer().getInventory().get put hotbar here so the building can be rotated
                            BuildFrame frame = new BuildFrame(building, p, hotbar, Integer.parseInt(args[1]));
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
        private Inventory hotbar;
        @Getter
        private int speed;
    }
    
    public static class MBOptions implements OptionClickEventHandler{
    
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("New Plot")){
                if(e.getName().equalsIgnoreCase("Confirm and Claim Plot")){
                    final Plot p = (Plot) e.getData();
                    Plot.getAllPlots().add(p);
                    PlayerData pd = PlayerData.getData(e.getPlayer());
                    pd.getPlots().add(p);
                    e.getPlayer().sendMessage("You have claimed this plot");
                    e.getPlayer().sendMessage("Setting base");
                    final Polygon bounds = p.getBase();
                    int Xmax = Ints.max(bounds.xpoints);
                    int Zmax = Ints.max(bounds.ypoints);
                    for(int x = Ints.min(bounds.xpoints); x <= Xmax; x++){
                        for(int z = Ints.min(bounds.ypoints); z <= Zmax; z++){
                            if(bounds.contains(new Point(x,z)) || (bounds.contains(new Point(x-1,z)) || bounds.contains(new Point(x,z-1)) || bounds.contains(new Point(x-1,z-1)))){
                                Location l = new Location(p.getCenter().getWorld(), x, p.getCenter().getBlockY()-1, z);
                                l.getBlock().setType(Material.DIRT);
                                l.getBlock().setData((byte) 1);
                            }
                        }
                    }
                }
            }else if(e.getMenuName().equalsIgnoreCase("Set Plot Type")){
                Plot p = Plot.inPlot(e.getPlayer().getLocation());
                PlayerData pd = PlayerData.getData(e.getPlayer());
                if(e.getName().equalsIgnoreCase("Custom Contract")){
                    
                }else if(e.getName().equalsIgnoreCase("Demolish")){
                    
                }else if(e.getName().equalsIgnoreCase("Erase")){
//                    Plot newPlot = new Plot()
                    p.setEmpty(true);
                }else if(e.getName().equalsIgnoreCase("Wall")){
                    Wall newWall = new Wall(p, WallType.WALL);
                    newWall.setEmpty(false);
                    Plot.getAllPlots().remove(p);
                    Plot.getAllPlots().add(newWall);
                    pd.getPlots().remove(p);
                    pd.getPlots().add(newWall);
                    if(p.getMunicipal() != null){
                        p.getMunicipal().getWalls().getParts().get(WallType.WALL).add(newWall);
                    }
                    e.getPlayer().sendMessage("You have assigned this plot to be a wall segments.");
                    if(p.getMunicipal() == null){
                        e.getPlayer().sendMessage("This building is not part of a municipal, you have no NPCs to help you build it!");
                    }else if(p.getMunicipal().getStructures().containsKey(BuildersHut.class) && 
                            !p.getMunicipal().getStructures().get(BuildersHut.class).isEmpty()){
                        for(Structure st : p.getMunicipal().getStructures().get(BuildersHut.class)){
                            BuildersHut hut = (BuildersHut) st;
                            if(hut.hasMaterials(newWall.getClass())){
                                e.getPlayer().sendMessage("Your NPCs will start work imediatly");
                                return;
                            }
                        }
                        e.getPlayer().sendMessage("You dont have the needed resources to build this structure fully");
                    }
                    final Polygon bounds = p.getBase();
                    int Xmax = Ints.max(bounds.xpoints);
                    int Zmax = Ints.max(bounds.ypoints);
                    for(int x = Ints.min(bounds.xpoints); x <= Xmax; x++){
                        for(int z = Ints.min(bounds.ypoints); z <= Zmax; z++){
                            if(bounds.contains(new Point(x,z)) || (bounds.contains(new Point(x-1,z)) || bounds.contains(new Point(x,z-1)) || bounds.contains(new Point(x-1,z-1)))){
                                Location l = new Location(p.getCenter().getWorld(), x, p.getCenter().getBlockY()-1, z);
                                l.getBlock().setType(Material.COBBLESTONE);
                            }
                        }
                    }
                }else if(e.getName().equalsIgnoreCase("Wall with Door")){
                    Wall newWall = new Wall(p, WallType.GATE);
                    newWall.setEmpty(false);
                    Plot.getAllPlots().remove(p);
                    Plot.getAllPlots().add(newWall);
                    pd.getPlots().remove(p);
                    pd.getPlots().add(newWall);
                    if(p.getMunicipal() != null){
                        p.getMunicipal().getWalls().getParts().get(WallType.GATE).add(newWall);
                    }
                    e.getPlayer().sendMessage("You have assigned this plot to be a wall segments.");
                    if(p.getMunicipal() == null){
                        e.getPlayer().sendMessage("This building is not part of a municipal, you have no NPCs to help you build it!");
                    }else if(p.getMunicipal().getStructures().containsKey(BuildersHut.class) && 
                            !p.getMunicipal().getStructures().get(BuildersHut.class).isEmpty()){
                        for(Structure st : p.getMunicipal().getStructures().get(BuildersHut.class)){
                            BuildersHut hut = (BuildersHut) st;
                            if(hut.hasMaterials(newWall.getClass())){
                                e.getPlayer().sendMessage("Your NPCs will start work imediatly");
                                return;
                            }
                        }
                        e.getPlayer().sendMessage("You dont have the needed resources to build this structure fully");
                    }
                    final Polygon bounds = p.getBase();
                    int Xmax = Ints.max(bounds.xpoints);
                    int Zmax = Ints.max(bounds.ypoints);
                    for(int x = Ints.min(bounds.xpoints); x <= Xmax; x++){
                        for(int z = Ints.min(bounds.ypoints); z <= Zmax; z++){
                            if(bounds.contains(new Point(x,z)) || (bounds.contains(new Point(x-1,z)) || bounds.contains(new Point(x,z-1)) || bounds.contains(new Point(x-1,z-1)))){
                                Location l = new Location(p.getCenter().getWorld(), x, p.getCenter().getBlockY()-1, z);
                                l.getBlock().setType(Material.PRISMARINE);
                            }
                        }
                    }
                }else if(e.getName().equalsIgnoreCase("Corner")){
                    Wall newWall = new Wall(p, WallType.CORNER);
                    newWall.setEmpty(false);
                    Plot.getAllPlots().remove(p);
                    Plot.getAllPlots().add(newWall);
                    pd.getPlots().remove(p);
                    pd.getPlots().add(newWall);
                    if(p.getMunicipal() != null){
                        p.getMunicipal().getWalls().getParts().get(WallType.CORNER).add(newWall);
                    }
                    e.getPlayer().sendMessage("You have assigned this plot to be a wall segments.");
                    if(p.getMunicipal() == null){
                        e.getPlayer().sendMessage("This building is not part of a municipal, you have no NPCs to help you build it!");
                    }else if(p.getMunicipal().getStructures().containsKey(BuildersHut.class) && 
                            !p.getMunicipal().getStructures().get(BuildersHut.class).isEmpty()){
                        for(Structure st : p.getMunicipal().getStructures().get(BuildersHut.class)){
                            BuildersHut hut = (BuildersHut) st;
                            if(hut.hasMaterials(newWall.getClass())){
                                e.getPlayer().sendMessage("Your NPCs will start work imediatly");
                                return;
                            }
                        }
                        e.getPlayer().sendMessage("You dont have the needed resources to build this structure fully");
                    }
                    final Polygon bounds = p.getBase();
                    int Xmax = Ints.max(bounds.xpoints);
                    int Zmax = Ints.max(bounds.ypoints);
                    for(int x = Ints.min(bounds.xpoints); x <= Xmax; x++){
                        for(int z = Ints.min(bounds.ypoints); z <= Zmax; z++){
                            if(bounds.contains(new Point(x,z)) || (bounds.contains(new Point(x-1,z)) || bounds.contains(new Point(x,z-1)) || bounds.contains(new Point(x-1,z-1)))){
                                Location l = new Location(p.getCenter().getWorld(), x, p.getCenter().getBlockY()-1, z);
                                l.getBlock().setType(Material.SMOOTH_BRICK);
                            }
                        }
                    }
                }else if(e.getName().equalsIgnoreCase("Tower")){
                    Wall newWall = new Wall(p, WallType.TOWER);
                    newWall.setEmpty(false);
                    Plot.getAllPlots().remove(p);
                    Plot.getAllPlots().add(newWall);
                    pd.getPlots().remove(p);
                    pd.getPlots().add(newWall);
                    if(p.getMunicipal() != null){
                        p.getMunicipal().getWalls().getParts().get(WallType.TOWER).add(newWall);
                    }
                    e.getPlayer().sendMessage("You have assigned this plot to be a wall segments.");
                    if(p.getMunicipal() == null){
                        e.getPlayer().sendMessage("This building is not part of a municipal, you have no NPCs to help you build it!");
                    }else if(p.getMunicipal().getStructures().containsKey(BuildersHut.class) && 
                            !p.getMunicipal().getStructures().get(BuildersHut.class).isEmpty()){
                        for(Structure st : p.getMunicipal().getStructures().get(BuildersHut.class)){
                            BuildersHut hut = (BuildersHut) st;
                            if(hut.hasMaterials(newWall.getClass())){
                                e.getPlayer().sendMessage("Your NPCs will start work imediatly");
                                return;
                            }
                        }
                        e.getPlayer().sendMessage("You dont have the needed resources to build this structure fully");
                    }
                    final Polygon bounds = p.getBase();
                    int Xmax = Ints.max(bounds.xpoints);
                    int Zmax = Ints.max(bounds.ypoints);
                    for(int x = Ints.min(bounds.xpoints); x <= Xmax; x++){
                        for(int z = Ints.min(bounds.ypoints); z <= Zmax; z++){
                            if(bounds.contains(new Point(x,z)) || (bounds.contains(new Point(x-1,z)) || bounds.contains(new Point(x,z-1)) || bounds.contains(new Point(x-1,z-1)))){
                                Location l = new Location(p.getCenter().getWorld(), x, p.getCenter().getBlockY()-1, z);
                                l.getBlock().setType(Material.BRICK);
                            }
                        }
                    }
                }else{
                    try {
                        Class structure = Class.forName("io.dallen.Kingdoms.Kingdom.Structures.Types."+e.getName().replace(" ", "").replace("'", ""));
                        Constructor constructor = structure.getConstructor(new Class[] {Plot.class});
                        Plot newPlot = (Plot) constructor.newInstance(p);
                        newPlot.setEmpty(false);
                        Plot.getAllPlots().remove(p);
                        Plot.getAllPlots().add(newPlot);
                        pd.getPlots().remove(p);
                        pd.getPlots().add(newPlot);
                        e.getPlayer().sendMessage("You have assigned this plot to be a " + e.getName() +".");
                        if(p.getMunicipal() == null){
                            e.getPlayer().sendMessage("This building is not part of a municipal, you have no NPCs to help you build it!");
                        }else if(p.getMunicipal().getStructures().containsKey(BuildersHut.class) && 
                                !p.getMunicipal().getStructures().get(BuildersHut.class).isEmpty()){
                            for(Structure st : p.getMunicipal().getStructures().get(BuildersHut.class)){
                                BuildersHut hut = (BuildersHut) st;
                                if(hut.hasMaterials(newPlot.getClass())){
                                    e.getPlayer().sendMessage("Your NPCs will start work imediatly");
                                    return;
                                }
                            }
                            e.getPlayer().sendMessage("You dont have the needed resources to build this structure fully");
                        }
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException 
                            | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                        e.getPlayer().sendMessage("Building name not found!");
                        Logger.getLogger(MultiBlockHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }else if(e.getMenuName().equalsIgnoreCase("Edit Plot Default")){
                e.getPlayer().sendMessage("To start a building contruction type the schematic name and tick in chat");
                Plot p = Plot.inPlot(e.getPlayer().getLocation());
                StringInput in = new StringInput("buildConst", p);
                openInputs.put(e.getPlayer().getName(), in);
            }
        }
    }
    
    public static class BuildTask implements Runnable{
        
        private int x = 0;
        
        private int y = 0; 
        
        private int z = 0;
        
        private Blueprint Building;
        
        private Location startCorner;
        
        private NPC Builder;
        
        private boolean running = true;
        
        public BuildTask(Blueprint building, Location start, int speed){
            Builder = Main.getNPCs().getNPCreg().createNPC(EntityType.PLAYER, "BingRazer");
            Builder.spawn(start);
            this.Building = building;
            this.startCorner = start;
            Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), this, speed, speed);
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
