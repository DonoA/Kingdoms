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

import io.dallen.Kingdoms.Kingdom.Plot;
import static io.dallen.Kingdoms.Kingdom.Structures.Structure.EditPlot;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class TownHall extends Plot{
    
    static{
        EditPlot.setName("Town Hall");
        EditPlot.setHandler(new MenuHandler());
    }
    
    public TownHall(Plot p){
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
    }
    
    @Override
    public void sendEditMenu(Player p){
        EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Create Municipal", this);
        super.sendEditMenu(p);
    }
    
    public static class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(ChestGUI.OptionClickEvent e){
            if(e.getName().equalsIgnoreCase("Create Municipal")){
                Plot p = (Plot) e.getData();
                p.createMucicpal();
            }
        }
    }
    
}
