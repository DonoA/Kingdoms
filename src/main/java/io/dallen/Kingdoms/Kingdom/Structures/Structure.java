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
package io.dallen.Kingdoms.Kingdom.Structures;

import com.google.common.primitives.Ints;
import io.dallen.Kingdoms.Handlers.BuildingHandler;
import io.dallen.Kingdoms.Handlers.MultiBlockHandler;
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Structures.Types.BuildersHut;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonMunicipality;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonStructure;
import io.dallen.Kingdoms.Storage.SaveTypes;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import java.awt.Point;
import java.awt.Polygon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class Structure implements SaveTypes.Saveable{
    
    @Getter @Setter
    private int Width; // X
    @Getter @Setter
    private int Height; // Y
    @Getter @Setter
    private int Length; // Z
    @Getter @Setter
    private Polygon Base; // Z
    @Getter
    private Location Center;
    @Getter @Setter
    private int ID;
    @Getter @Setter
    private Player Owner;
    @Getter @Setter
    private Kingdom Kingdom;
    @Getter @Setter
    private Municipality Municipal;
    @Getter @Setter
    private int Area;
    @Getter @Setter
    private int Rank;
    @Getter @Setter
    private int maxRank;
    
    public static ChestGUI EditPlot;
    public static ChestGUI BuildMenu;
    
    @Getter
    private long amountBuilt;
    
    static {
//        EditPlot = new ChestGUI("Edit Plot Default", 2, new MenuHandler()){{
//            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
//            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Upgrade");
//            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
//        }};
        BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Default Wall 1");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Default Wall 2");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Other");
        }};
    }
    
    public Structure(Polygon base, Location cent, Player own, Kingdom kingdom, Municipality municipal){
        this.Center = cent;
        this.Owner = own;
        this.Kingdom = kingdom;
        this.Municipal = municipal;
        this.Base = base;
        setArea();
        
    }
    
    public Structure(Polygon base, Location cent, Player own, Municipality municipal){
        this.Base = base;
        this.Center = cent;
        this.Owner = own;
        this.Municipal = municipal;
        setArea();
    }
    
    public Structure(Polygon base, Location cent, Player own, Kingdom kingdom){
        this.Base = base;
        this.Center = cent;
        this.Owner = own;
        this.Kingdom = kingdom;
        setArea();
    }
    
    /*
     * This method MUST compile the needed menu for sending and place the relevent options in the data slots (so they are player specific)
     * as it turns out handling menus from the server side is terrible
     */
    public void sendEditMenu(Player p){
        throw new UnsupportedOperationException();
    }
    
    /*
     * This method MUST compile the needed menu for sending and place the relevent options in the data slots (so they are player specific)
     * as it turns out handling menus from the server side is terrible
     */
    public void sendBuildMenu(Player p){
        BuildMenu.setMenuData(this);
        BuildMenu.sendMenu(p);
    }
    
    private void setArea(){
        int Xmax = Ints.max(Base.xpoints);
        int Zmax = Ints.max(Base.ypoints);
        for(int x = Ints.min(Base.xpoints); x <= Xmax; x++){
            for(int z = Ints.min(Base.ypoints); z <= Zmax; z++){
                if(Base.contains(new Point(x,z)) || (Base.contains(new Point(x-1,z)) || Base.contains(new Point(x,z-1)) || Base.contains(new Point(x-1,z-1)))){
                    Area++;
                }
            }
        }
    }
    
    public JsonStructure toJsonObject(){
        throw new UnsupportedOperationException();
    }
    
    public static class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("Build Options")){
                if(e.getName().equalsIgnoreCase("Other")){
                    BuildingHandler.getBuildChestHandler().onOptionClick(e);
                }else{
                    e.getPlayer().sendMessage("Default option called");
                }
            }else if(e.getMenuName().equalsIgnoreCase("Edit Plot Default")){
                BuildingHandler.chestBuildOptions(e, BuildMenu);
            }
        }
    }
}
