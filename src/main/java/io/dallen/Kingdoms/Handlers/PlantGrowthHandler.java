/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of Kingdoms for the Morphics Network.
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
package io.dallen.Kingdoms.Handlers;

import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Farm;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.LogUtil;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

/**
 *
 * @author Donovan Allen
 */
public class PlantGrowthHandler implements Listener{
    
    private static ArrayList<Material> cropTypes = new ArrayList<Material>(Arrays.asList(new Material[] 
                            {Material.CROPS, Material.POTATO, Material.CARROT, Material.NETHER_WARTS}));
    
    @EventHandler
    public void onBlockGrow(BlockGrowEvent e){
        if(cropTypes.contains(e.getBlock().getType())){
            ItemStack rtnStack = null;
            boolean grown = false;
            switch(e.getBlock().getType()){
                case CROPS:
                    grown = e.getBlock().getData() == (byte)6;
                    rtnStack = new ItemStack(Material.WHEAT, (int) Math.ceil(Math.random() * 3) - 1);
                    break;
                case POTATO:
                    grown = e.getBlock().getData() == (byte)6;
                    rtnStack = new ItemStack(Material.POTATO_ITEM, (int) Math.ceil(Math.random() * 4) - 1);
                    break;
                case CARROT:
                    grown = e.getBlock().getData() == (byte)6;
                    rtnStack = new ItemStack(Material.CARROT_ITEM, (int) Math.ceil(Math.random() * 4) - 1);
                    break;
                case NETHER_WARTS:
                    grown = e.getBlock().getData() == (byte)2;
                    rtnStack = new ItemStack(Material.NETHER_WARTS, (int) Math.ceil(Math.random() * 2) - 1);
                    break;
            }
            Plot p = null;
            if(grown && (p = Plot.inPlot(e.getBlock().getLocation())) != null && p instanceof Farm){
                final Farm f = (Farm) p;
                if(f.isGrowing()){
                    final ItemStack rtn = rtnStack;
                    final BlockGrowEvent ev = e;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
                        @Override
                        public void run() {
                            ev.getBlock().setData((byte) 0);
                            f.getStorage().addItem(rtn);
                        }
                    
                    }, 200);

                }
            }
        }
    }
}
