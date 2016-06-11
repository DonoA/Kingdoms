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
package io.dallen.Kingdoms.Kingdom;

import com.google.common.primitives.Ints;
import io.dallen.Kingdoms.Handlers.BuildingHandler;
import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Kingdom.Structures.Contract;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.BuildersHut;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.Wall;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.WallType;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import io.dallen.Kingdoms.Util.LocationUtil;
import java.awt.Point;
import java.awt.Polygon;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author donoa_000
 */
public class Plot extends Structure implements Listener{
    
    @Getter
    private static ArrayList<Plot> allPlots = new ArrayList<Plot>();
    
    @Getter
    private ArrayList<Contract> contracts = new ArrayList<Contract>();
    
    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;
    
    @Getter @Setter
    private boolean empty = true;
    
    public Plot(Polygon base, Location cent, OfflinePlayer own, Municipality mun) {
        super(base, cent, own, mun);
    }
    
    public Plot(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getKingdom(), p.getMunicipal());
    }
    
    public static Plot inPlot(Location l){
        for(Plot p : allPlots){
            if(p.getBase().contains(new Point(l.getBlockX(),l.getBlockZ())) || 
                   (p.getBase().contains(new Point(l.getBlockX()-1,l.getBlockZ())) || 
                    p.getBase().contains(new Point(l.getBlockX(),l.getBlockZ()-1)) || 
                    p.getBase().contains(new Point(l.getBlockX()-1,l.getBlockZ()-1)))){
                return p;
            }
        }
        return null;
    }
    
    public boolean contains(Location l){
        return super.getBase().contains(new Point(l.getBlockX(),l.getBlockZ())) || 
              (super.getBase().contains(new Point(l.getBlockX()-1,l.getBlockZ())) || 
               super.getBase().contains(new Point(l.getBlockX(),l.getBlockZ()-1)) || 
               super.getBase().contains(new Point(l.getBlockX()-1,l.getBlockZ()-1)));
    }
    
    public boolean isValid(){
        for(Plot p : allPlots){
            if(p.getBase().intersects(super.getBase().getBounds())){
                return false;
            }
        }
        return true;
    }
    
    public boolean createMucicpal(){
        if(super.getMunicipal() != null){
            return false;
        }
        super.setMunicipal(new Municipality((Structure) this));
        return true;
    }
    
    public boolean canBuild(Player p){
        return super.getOwner().equals(p);
    }
    
    public static class MenuHandler implements OptionClickEventHandler{
        
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
                    for(int i = 0; i < p.getBase().npoints; i++){
                        Location torch = LocationUtil.asLocation(new Point(p.getBase().xpoints[i], p.getBase().ypoints[i]), 
                                p.getCenter().getWorld(), p.getCenter().getBlockY());
                        if(torch.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){
                            final Location t = torch;
                            Bukkit.getScheduler().runTask(Main.getPlugin(), new Runnable(){
                                @Override
                                public void run() {
                                    t.getBlock().breakNaturally();
                                }
                            });
                        }
                    }
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
                    for(Municipality m : Municipality.getAllMunicipals()){
                        if(m.getInfluence().intersects(p.getBase().getBounds2D())){
                            p.setMunicipal(m);
                            m.addStructure(p);
                        }
                    }
                }
            }else if(e.getMenuName().equalsIgnoreCase("Set Plot Type")){
                Plot p = Plot.inPlot(e.getPlayer().getLocation());
                PlayerData pd = PlayerData.getData(e.getPlayer());
                if(e.getName().equalsIgnoreCase("Custom Contract")){
                    
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
                }else if(e.getName().equalsIgnoreCase("Demolish") || e.getName().equalsIgnoreCase("Erase")){
                    BuildingHandler.chestBuildOptions(e, null);
                }else{
                    try {
                        Class structure = Class.forName("io.dallen.Kingdoms.Kingdom.Structures.Types."+e.getName().replace(" ", "").replace("'", ""));
                        Constructor constructor = structure.getConstructor(new Class[] {Plot.class});
                        Plot newPlot = (Plot) constructor.newInstance(p);
                        if(newPlot.getMunicipal() != null){
                            newPlot.getMunicipal().addStructure(newPlot);
                        }
                        newPlot.setEmpty(false);
                        Plot.getAllPlots().remove(p);
                        Plot.getAllPlots().add(newPlot);
                        pd.getPlots().remove(p);
                        pd.getPlots().add(newPlot);
                        e.getPlayer().sendMessage("You have assigned this plot to be a " + e.getName() +".");
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException 
                            | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                        e.getPlayer().sendMessage("Building name not found!");
                    }
                }
            }
        }
    }
    
}
