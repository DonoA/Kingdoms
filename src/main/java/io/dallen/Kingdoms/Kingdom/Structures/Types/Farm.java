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

import io.dallen.Kingdoms.Handlers.BuildingHandler;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allows the kingdom to create and maintain crops
 * 
 * @author donoa_000
 */
public class Farm extends Plot{

    private int maxFarmLand;
    
    private int maxResidents;
    
    private int currentFarmLand;
    
    private int currentResidents;
    
    private int currentlyPlanted;
    
    private FoodStats localStock;
    
    private boolean growing;
    
    @Getter
    private static ChestGUI EditPlot;
    
    static {
        EditPlot = new ChestGUI("TownHall", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }};
    }
    
    public Farm(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
    }
    
    @Override
    public void sendEditMenu(Player p){
        if(growing)
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Stop Growing");
        else
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Start Growing");
        EditPlot.setMenuData(this);
        EditPlot.sendMenu(p);
    }
    
    private static class FoodStats{
        
        private int Wheat;
        
        private int Apples;
        
        private int Carrots;
        
        private int Potatoes;
        
    }
    
    public static class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getName().equalsIgnoreCase("Stop Growing")){
                
            }else if(e.getName().equalsIgnoreCase("Start Growing")){
                
            }else{
                BuildingHandler.chestBuildOptions(e, BuildMenu);
            }
        }
    }
    
}
