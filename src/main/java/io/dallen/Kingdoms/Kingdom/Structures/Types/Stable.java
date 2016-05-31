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
import static io.dallen.Kingdoms.Kingdom.Structures.Structure.BuildMenu;
import io.dallen.Kingdoms.Util.ChestGUI;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allows the kingdom to train and hold horses
 * 
 * @author donoa_000
 */
public class Stable extends Plot{
    
    private int horseCapacity;
    
    private int workerCapacity;
    
    private int currentHorses; // may be changed if horses are going to owned personally (which they should be)
    
    private int currentWorkers;
    
    @Getter
    private static ChestGUI EditPlot;
    
    static{
        EditPlot = new ChestGUI("Stable", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Upgrade");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }};
    }

    public Stable(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
    }
    
    @Override
    public void sendEditMenu(Player p){
        EditPlot.setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Mount Horse", this);
        EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Mount Army Horse", this);
        EditPlot.setOption(5, new ItemStack(Material.ENCHANTED_BOOK), "Mount King Horse", this);
        EditPlot.setMenuData(this);
        EditPlot.sendMenu(p);
    }
    
    public static class MenuHandler implements ChestGUI.OptionClickEventHandler{
        
        @Override
        public void onOptionClick(ChestGUI.OptionClickEvent e){
            if(e.getName().contains("Mount")){
                Horse horse = (Horse) e.getPlayer().getLocation().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.HORSE);
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                if(e.getName().equalsIgnoreCase("Mount Army Horse")){
                    horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
                }else if(e.getName().equalsIgnoreCase("Mount King Horse")){
                    horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING));
                }
                horse.setOwner((AnimalTamer) e.getPlayer());
                horse.setPassenger(e.getPlayer());
            }else{
                BuildingHandler.chestBuildOptions(e, BuildMenu);
            }
        }
    }
    
}
