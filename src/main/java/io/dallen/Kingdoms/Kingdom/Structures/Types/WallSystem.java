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
package io.dallen.Kingdoms.Kingdom.Structures.Types;

import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import static io.dallen.Kingdoms.Kingdom.Structures.Structure.BuildMenu;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonWallSystem;
import io.dallen.Kingdoms.Storage.SaveTypes;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.LocationUtil;
import io.dallen.Kingdoms.Util.LogUtil;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class WallSystem implements SaveTypes.Saveable{
    
    
    
    @Getter
    private HashMap<WallType, ArrayList<Wall>> Parts = new HashMap<WallType, ArrayList<Wall>>();
    
    @Getter
    private Municipality municipal;
    
    public WallSystem(Municipality m) {
        this.municipal = m;
        Parts.put(WallType.WALL, new ArrayList<Wall>());
        Parts.put(WallType.CORNER, new ArrayList<Wall>());
        Parts.put(WallType.GATE, new ArrayList<Wall>());
        Parts.put(WallType.TOWER, new ArrayList<Wall>());
    }
    
    public boolean recalculateBase(){//Should be called async if possible
        ArrayList<Wall> corners = new ArrayList<Wall>();
        corners.addAll(Parts.get(WallType.CORNER));
        corners.addAll(Parts.get(WallType.TOWER));
        int[] Xs = new int[corners.size()];
        int[] Zs = new int[corners.size()];
        int i = 0;
        for(i = 0; i < corners.size(); i++){
            Xs[i] = (int) LocationUtil.asPoint(corners.get(i).getCenter()).getX();
            Zs[i] = (int) LocationUtil.asPoint(corners.get(i).getCenter()).getY();
        }
        Polygon newBase = new Polygon(Xs, Zs, i);
        municipal.setBase(newBase);
        return true;
    }
    
    public JsonWallSystem toJsonObject(){
        throw new UnsupportedOperationException();
    }
    
    public static enum WallType {
        WALL, GATE, TOWER, CORNER
    }
    
    public static class Wall extends Plot{
        @Getter
        private static ArrayList<String> damageBars = new ArrayList<String>();
        
        @Getter
        private ArrayList<Player> currInteracters = new ArrayList<Player>();
        
        @Getter
        private BossBar damageBar;
        
        @Getter
        private WallType type;
        
        @Getter
        private static ChestGUI EditPlot;

        static{
            EditPlot = new ChestGUI("Wall", 2, new MenuHandler()){{
                setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
                setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Upgrade");
                setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
            }};
        }
        
        public Wall(Plot p, WallType type){
            super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
            this.type = type;
        }
        
        public void damage(){
            damageBar.setProgress(damageBar.getProgress()-1);
        }
        
        public void repair(){
            damageBar.setProgress(damageBar.getProgress()+1);
        }
        
        @Override
        public void sendEditMenu(Player p){
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Train Builder", this);
            EditPlot.setMenuData(this);
            EditPlot.sendMenu(p);
        }

        public static class MenuHandler implements ChestGUI.OptionClickEventHandler{

            @Override
            public void onOptionClick(ChestGUI.OptionClickEvent e){
                if(e.getMenuName().equalsIgnoreCase("Wall")){
                    if(e.getName().equalsIgnoreCase("Build")){
                        LogUtil.printDebug(((Structure) e.getMenuData()).getMunicipal());
                        LogUtil.printDebug(((Structure) e.getMenuData()).getMunicipal().getStructures().toString());
                        if(((Structure) e.getMenuData()).getMunicipal() != null && 
                            !((Structure) e.getMenuData()).getMunicipal().getStructures().get(BuildersHut.class).isEmpty()){
                            BuildMenu.setMenuData(e.getMenuData());
                            e.setNext(BuildMenu);
                        }else{
                            e.getPlayer().sendMessage("You have no NPCs to build this!");
                        }
                    }else if(e.getName().equalsIgnoreCase("Erase")){
                        e.getPlayer().sendMessage("Default option called");
                    }else if(e.getName().equalsIgnoreCase("Demolish")){
                        e.getPlayer().sendMessage("Default option called");
                    }
                }
            }
        }
    }
    
}
