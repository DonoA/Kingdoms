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
package io.dallen.kingdoms.core.Structures.Types;


import io.dallen.kingdoms.core.Handlers.BuildMenuHandler;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEvent;
import io.dallen.kingdoms.utilities.Utils.ChestGUI.OptionClickEventHandler;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allows the kingdom to imprison those that deserve it
 * 
 * @author donoa_000
 */
public class Dungeon extends Plot{

    private int capacity;
    
    private int security;
    
    private int guards;
    
    private int cells;
    
    private int currentInmates;
    
    private int currentGuards;
    
    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;
    
    public Dungeon(Plot p) {
        super(p);
        EditPlot = new ChestGUI("Dungeon", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }};
        
        BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()){{
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Other");
        }};
    }
    
    @Override
    public void sendEditMenu(Player p){
        EditPlot.sendMenu(p);
    }
    
    public class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equals(EditPlot.getName())){
                BuildMenuHandler.chestBuildOptions(e, Dungeon.this);
            }
        }
    }

}
