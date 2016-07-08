/*
 * Copyright 2016 Morphics Network.
 * 
 * This file is part of KingdomsCore for the Morphics Network.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.kingdoms.core.Handlers;

import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.core.Structures.Types.BuildersHut;
import io.dallen.kingdoms.utilities.Utils.ChestGUI;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class BuildMenuHandler {
    
    public static void chestBuildOptions(ChestGUI.OptionClickEvent e, Structure struc){
        ChestGUI buildMenu = null;
        if(e.getName().equalsIgnoreCase("Build")){
            if(struc.getMunicipal() != null && 
                !struc.getMunicipal().getStructures().get(BuildersHut.class).isEmpty()){
                buildMenu.setMenuData(struc);
                int i = 0;
                for(File f : new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + 
                        struc.getClass().getName() + DBmanager.getFileSep()).listFiles()){
                    if(i<9){
                        buildMenu.setOption(0*9+i, new ItemStack(Material.ENCHANTED_BOOK), f.getName());
                    }else{
                        break;
                    }
                }
                e.setNext(buildMenu);
            }else{
                e.getPlayer().sendMessage("You have no NPCs to build this!");
            }
        }else if(e.getName().equalsIgnoreCase("Erase")){
            Plot p = (Plot) e.getMenuData();
            double halfWidth = p.getWidth()/2;
            double halfLength = p.getLength()/2;
            for(int x = (int) -halfWidth; x <= Math.ceil(halfWidth)+1; x++){
                for(int z = (int) -halfLength; z <= Math.ceil(halfLength)+1; z++){
                    for(int y = 0; y < 50; y++){
                        if(!p.getCenter().clone().add(x, y, z).getBlock().getType().equals(Material.AIR)){
                            p.getCenter().clone().add(x, y, z).getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }else if(e.getName().equalsIgnoreCase("Demolish")){
            Plot p = (Plot) struc;
            if(Plot.getAllPlots().contains(p))
                Plot.getAllPlots().remove(p);
            if(p.getMunicipal() != null){
                for(ArrayList<Structure> structs : p.getMunicipal().getStructures().values()){
                    if(structs.contains(p)){
                        structs.remove(p);
                    }
                }
            }
        }
    }
    
    
    
}
