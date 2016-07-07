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
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonLocation;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonPolygon;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonStructure;
import io.dallen.Kingdoms.Storage.SaveType;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import io.dallen.Kingdoms.Util.LogUtil;
import java.awt.Point;
import java.awt.Polygon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
@NoArgsConstructor
public abstract class Structure implements SaveType.Saveable{
    
    @Getter @Setter
    private static int currentID = 0;
    
    @Getter @Setter
    private int Width; // X
    @Getter @Setter
    private int Height; // Y
    @Getter @Setter
    private int Length; // Z
    @Getter @Setter
    private Polygon Base; // Z
    @Getter @Setter
    private Location Center;
    @Getter @Setter
    private OfflinePlayer Owner;
    @Getter @Setter
    private Kingdom Kingdom;
    @Getter @Setter
    private Municipality Municipal;
    @Getter @Setter
    private int Area;
    @Getter @Setter
    private int StructureID;
    @Getter
    private OptionClickEventHandler MenuHandler;
//    @Getter @Setter
//    private int Rank;
//    @Getter @Setter
//    private int maxRank;
    
    
    @Getter
    private long amountBuilt;
    
    public Structure(Polygon base, Location cent, OfflinePlayer own){
        this.Center = cent;
        this.Owner = own;
        this.Base = base;
        LogUtil.printDebug("new struct " + currentID);
        this.StructureID = currentID;
        currentID++;
        setArea();
        
    }
    
    public Structure(Polygon base, Location cent, OfflinePlayer own, int ID){
        this.Base = base;
        this.Center = cent;
        this.Owner = own;
        this.StructureID = ID;
        setArea();
    }
    
    public boolean contains(Point p){
        return Base.contains(p) || (Base.contains(new Point(p.x-1,p.y)) || Base.contains(new Point(p.x,p.y-1)) || 
               Base.contains(new Point(p.x-1,p.y-1)));
    }
    
    public void sendEditMenu(Player p){
        new ChestGUI("Structure", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }}.sendMenu(p);
    }
    
    public void sendBuildMenu(Player p){
        new ChestGUI("Build Options", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Default Building 1");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Default Building 2");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Other");
        }}.sendMenu(p);
    }
    
    public void setArea(){
        this.Width = (int) Math.round(Base.getBounds().getMaxX() - Base.getBounds().getMinX());
        this.Length = (int) Math.round(Base.getBounds().getMaxY() - Base.getBounds().getMinY());
        int Xmax = (int) Math.round(Base.getBounds().getMaxX());
        int Zmax = (int) Math.round(Base.getBounds().getMaxY());
        for(int x = Ints.min(Base.xpoints); x <= Xmax; x++){
            for(int z = Ints.min(Base.ypoints); z <= Zmax; z++){
                if(Base.contains(new Point(x,z)) || (Base.contains(new Point(x-1,z)) || Base.contains(new Point(x,z-1)) || Base.contains(new Point(x-1,z-1)))){
                    Area++;
                }
            }
        }
    }
    
    @Override
    public JsonStructure toJsonObject(){
        JsonStructure js = new JsonStructure();
        js.setHeight(Height);
        js.setWidth(Width);
        js.setLength(Length);
        js.setOwner(Owner.getUniqueId());
        if(this instanceof WallSystem.Wall){
            js.setType(WallSystem.Wall.class.getName());
        }
        boolean classFound = false;
        for(Class c : Main.getStructureClasses()){
            if(this.getClass().isAssignableFrom(c) && !classFound){
                js.setType(c.getName());
                classFound = true;
            }
        }
        if(this instanceof Plot && !classFound){
            js.setType(Plot.class.getName());
        }
        if(Municipal != null)
            js.setMunicipal(Municipal.getMunicipalID());
        else
            js.setMunicipal(-1);
        if(Kingdom != null)
            js.setKingdom(Kingdom.getKingdomID());
        else
            js.setKingdom(-1);
        js.setBase(new JsonPolygon(Base));
        js.setCenter(new JsonLocation(Center));
        js.setStructureID(StructureID);
        return js;
    }
    
    
    public class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("Build Options")){
                if(e.getName().equalsIgnoreCase("Other")){
                    BuildingHandler.getBuildChestHandler().onOptionClick(e, Structure.this);
                }else{
                    e.getPlayer().sendMessage("Default option called");
                }
            }else if(e.getMenuName().equalsIgnoreCase("Edit Plot Default")){
                BuildingHandler.chestBuildOptions(e, new ChestGUI("Build Options", 2, new MenuHandler()){{
                    setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Default Wall 1");
                    setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Default Wall 2");
                    setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Other");
                }}, Structure.this);
            }
        }
    }
}
