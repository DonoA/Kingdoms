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
import io.dallen.Kingdoms.Kingdom.Structures.Contract;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.BuildersHut;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.Wall;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.WallType;
import io.dallen.Kingdoms.Overrides.KingdomMaterial;
import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import java.awt.Point;
import java.awt.Polygon;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
    
    public Plot(Polygon base, Location cent, OfflinePlayer own) {
        super(base, cent, own);
        defEditMenu();
    }
    
    public Plot(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getStructureID());
        defEditMenu();
    }
    
    public Plot() {
        super();
        defEditMenu();
    }
    
    
    protected void defEditMenu(){
        EditPlot = new ChestGUI("Set Plot Type", 4, new MenuHandler()) {{
            setOption(9*0 + 1, KingdomMaterial.STRUCTURE_STOREROOM.getItemStack(), "Storeroom");
            setOption(9*0 + 2, KingdomMaterial.STRUCTURE_BARRACKS.getItemStack(), "Barracks");
            setOption(9*0 + 3, KingdomMaterial.STRUCTURE_TRAININGGROUND.getItemStack(), "Training Ground");
            setOption(9*0 + 4, KingdomMaterial.STRUCTURE_TOWNHALL.getItemStack(), "Town Hall");
            setOption(9*0 + 5, KingdomMaterial.STRUCTURE_BLACKSMITH.getItemStack(), "Blacksmith");
            setOption(9*0 + 6, KingdomMaterial.STRUCTURE_FARM.getItemStack(), "Farm");
            setOption(9*0 + 7, KingdomMaterial.STRUCTURE_ARMORY.getItemStack(), "Armory");
            
            setOption(9*1 + 1, KingdomMaterial.STRUCTURE_BUILDERSHUT.getItemStack(), "Builder's Hut");
            setOption(9*1 + 2, KingdomMaterial.STRUCTURE_BANK.getItemStack(), "Bank");
            setOption(9*1 + 3, KingdomMaterial.STRUCTURE_STABLE.getItemStack(), "Stable");
            setOption(9*1 + 4, KingdomMaterial.STRUCTURE_DUNGEON.getItemStack(), "Dungeon");
            setOption(9*1 + 5, KingdomMaterial.STRUCTURE_MARKETPLACE.getItemStack(), "Marketplace");
            setOption(9*1 + 6, KingdomMaterial.STRUCTURE_POSTOFFICE.getItemStack(), "Post Office");
            setOption(9*1 + 7, KingdomMaterial.STRUCTURE_MINE.getItemStack(), "Mine");
            
            setOption(9*2 + 2, KingdomMaterial.STRUCTURE_WALL_WALL.getItemStack(), "Wall");
            setOption(9*2 + 3, KingdomMaterial.STRUCTURE_WALL_GATE.getItemStack(), "Gate");
            setOption(9*2 + 4, KingdomMaterial.STRUCTURE_WALL_CORNER.getItemStack(), "Corner");
            setOption(9*2 + 5, KingdomMaterial.STRUCTURE_WALL_TOWER.getItemStack(), "Tower");
            
            setOption(9*3 + 3, new ItemStack(Material.PAPER), "Custom Contract");
            setOption(9*3 + 4, KingdomMaterial.DEMOLISH.getItemStack(), "Demolish");
            setOption(9*3 + 5, KingdomMaterial.ERASE.getItemStack(), "Erase");
        }};
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
    
    @Override
    public void sendEditMenu(Player p){
        EditPlot.setMenuData(this);
        EditPlot.sendMenu(p);
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
    
//    @Override
//    public JsonStructure toJsonObject(){
//        JsonStructure js = new JsonStructure();
//        return js;
//    }
    
    public class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("Set Plot Type")){
                Plot p = Plot.inPlot(e.getPlayer().getLocation());
                PlayerData pd = PlayerData.getData(e.getPlayer());
                if(e.getName().equalsIgnoreCase("Custom Contract")){
                    
                }else if(e.getName().equalsIgnoreCase("Wall") || e.getName().equalsIgnoreCase("Gate") || e.getName().equalsIgnoreCase("Corner") || e.getName().equalsIgnoreCase("Tower")){
                    
                    Wall newWall = new Wall(p, WallType.valueOf(e.getName().toUpperCase()));
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
