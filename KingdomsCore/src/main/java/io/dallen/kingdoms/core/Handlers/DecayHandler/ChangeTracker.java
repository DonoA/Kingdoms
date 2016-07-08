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
package io.dallen.kingdoms.core.Handlers.DecayHandler;

import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import io.dallen.kingdoms.utilities.Utils.PermissionManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author donoa_000
 */
public class ChangeTracker implements Listener{
    
    @Getter
    private static HashMap<Location, SaveBlock> Changes = new HashMap<Location, SaveBlock>();
    
    @Getter
    private static volatile ArrayList<Location> forDecay = new ArrayList<Location>();
    
    @Getter
    private static Runnable MarkDecayBlocks = new Runnable(){
            @Override
            public void run(){
                if(!forDecay.isEmpty()){
                    for(Location l : (ArrayList<Location>) forDecay.clone()){
                        if(!Changes.containsKey(l)){
                            forDecay.remove(l);
                        }
                    }
                }
                if(!Changes.isEmpty()){
                    for(Entry<Location, SaveBlock> e : Changes.entrySet()){
                        if(new Date(System.currentTimeMillis() - 1000*60*60*48).after(e.getValue().getBreakDate())){
                            forDecay.add(e.getKey());
                        }
                    }
                }
            }
        };
    
    @Getter
    private static Runnable DecayBlocks = new Runnable(){
            @Override
            public void run(){
                if(!forDecay.isEmpty()){
                    for(Location l : forDecay){
                        SaveBlock sb = Changes.get(l);
                        l.getBlock().setType(sb.getBlock());
                        l.getBlock().setData(sb.getData());
                        Changes.remove(l);
                    }
                }
            }
        };
    
    public ChangeTracker(JavaPlugin p){
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(p, MarkDecayBlocks, 1, 20*60);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p, DecayBlocks, 2, 20*60);
        Bukkit.getPluginManager().registerEvents(this, p);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(e.getPlayer().hasPermission(PermissionManager.getBuildPermission())){
            e.setCancelled(false);
            return;
        }
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        LogUtil.printDebug("Block Place called");
        Block b = e.getBlockPlaced();
        if(p == null){
            if(Changes.containsKey(b.getLocation())){
                Changes.get(b.getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(b.getLocation(), new SaveBlock(b));
            }
        }else{
            if(!p.canBuild(e.getPlayer())){
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getPlayer().hasPermission(PermissionManager.getBuildPermission())){
            e.setCancelled(false);
            return;
        }
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        LogUtil.printDebug("Block Break called");
        Block b = e.getBlock();
        if(p == null){
            if(Changes.containsKey(b.getLocation())){
                Changes.get(b.getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(b.getLocation(), new SaveBlock(b));
            }
        }else{
            if(!p.canBuild(e.getPlayer())){
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e){
        LogUtil.printDebug("Block Burn called");
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        Block b = e.getBlock();
        if(p == null){
            if(Changes.containsKey(b.getLocation())){
                Changes.get(b.getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(b.getLocation(), new SaveBlock(b));
            }
        }else{
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e){
        LogUtil.printDebug("Block Explode called");
        for(Block b : e.blockList()){
            Plot p = Plot.inPlot(b.getLocation());
            if(p == null){
                if(Changes.containsKey(b.getLocation())){
                    Changes.get(b.getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
                }else{
                    Changes.put(b.getLocation(), new SaveBlock(b));
                }
            }else{
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockLeavesDecay(LeavesDecayEvent e){
        LogUtil.printDebug("Block Leave Decay called");
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        Block b = e.getBlock();
        if(p == null){
            if(Changes.containsKey(b.getLocation())){
                Changes.get(b.getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(b.getLocation(), new SaveBlock(b));
            }
        }
    }
    
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e){
        LogUtil.printDebug("Block Form called");
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        Block b = e.getBlock();
        if(p == null){
            if(Changes.containsKey(b.getLocation())){
                Changes.get(b.getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(b.getLocation(), new SaveBlock(b));
            }
        }
    }
    
    
    
    public static class SaveBlock{
        @Getter
        private Material Block;
        @Getter
        private byte Data;
        @Setter @Getter
        private Date breakDate;
        
        public SaveBlock(Block b){
            Block = b.getType();
            Data = b.getState().getRawData();
            breakDate = new Date(System.currentTimeMillis());
        }
    }
    
}
