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
import io.dallen.Kingdoms.Util.LogUtil;
import org.bukkit.CropState;
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
    
    @EventHandler
    public void onBlockGrow(BlockGrowEvent e){
        LogUtil.printDebug("block grow1");
        LogUtil.printDebug(e.getBlock().getState().getData() instanceof Crops);
        LogUtil.printDebug(((Crops) e.getBlock().getState().getData()).getState());
        if(e.getBlock().getState().getData() instanceof Crops && ((Crops) e.getBlock().getState().getData()).getState().equals(CropState.VERY_TALL)){
            LogUtil.printDebug("block grow2");
            Plot p = Plot.inPlot(e.getBlock().getLocation());
            if(p != null && p instanceof Farm){
                LogUtil.printDebug("block grow");
                Farm f = (Farm) p;
                if(f.isGrowing()){
                    f.getStorage().addItem(new ItemStack(e.getBlock().getType()));
                    Crops c = (Crops) e.getBlock().getState().getData();
                    c.setState(CropState.SEEDED);
                    e.getBlock().getState().setData(c);
                }
            }
        }
    }
}
