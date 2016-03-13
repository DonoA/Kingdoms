/*
 * This file is part of Kingdoms.
 * 
 * Kingdoms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdoms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdoms.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms.Handlers.DecayHandler;

import io.dallen.Kingdoms.Kingdom.Plot;
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
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author donoa_000
 */
public class ChangeTracker implements Listener{
    
    @Getter
    private static HashMap<Location, SaveBlock> Changes = new HashMap<Location, SaveBlock>();
    
    @Getter
    private static ArrayList<Location> forDecay = new ArrayList<Location>();
    
    @Getter
    private static Runnable MarkDecayBlocks = new Runnable(){
            @Override
            public void run(){
                for(Location l : forDecay){
                    if(!Changes.containsKey(l)){
                        forDecay.remove(l);
                    }
                }
                for(Entry<Location, SaveBlock> e : Changes.entrySet()){
                    if(new Date(System.currentTimeMillis() - 1000*60*60*48).after(e.getValue().getBreakDate())){
                        forDecay.add(e.getKey());
                    }
                }
            }
        };
    
    @Getter
    private static Runnable DecayBlocks = new Runnable(){
            @Override
            public void run(){
                for(Location l : forDecay){
                    SaveBlock sb = Changes.get(l);
                    l.getBlock().setType(sb.getBlock());
                    l.getBlock().setData(sb.getData());
                    Changes.remove(l);
                }
            }
        };
    
    public ChangeTracker(JavaPlugin p){
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(p, MarkDecayBlocks, 1, 20 * 60);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(p, DecayBlocks, 2, 20 * 60);
        Bukkit.getPluginManager().registerEvents(this, p);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        if(p == null){
            if(Changes.containsKey(e.getBlock().getLocation())){
                Changes.get(e.getBlock().getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(e.getBlock().getLocation(), new SaveBlock(e.getBlock()));
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        if(p == null && !(e.getBlock().getType().equals(Material.DIAMOND_ORE) ||
                          e.getBlock().getType().equals(Material.IRON_ORE))){//TODO add more here
            if(Changes.containsKey(e.getBlock().getLocation())){
                Changes.get(e.getBlock().getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(e.getBlock().getLocation(), new SaveBlock(e.getBlock()));
            }
        }
    }
    
    @EventHandler
    public void onBlockBurn(BlockBurnEvent e){
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        if(p == null){
            if(Changes.containsKey(e.getBlock().getLocation())){
                Changes.get(e.getBlock().getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(e.getBlock().getLocation(), new SaveBlock(e.getBlock()));
            }
        }
    }
    
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e){
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        if(p == null){
            if(Changes.containsKey(e.getBlock().getLocation())){
                Changes.get(e.getBlock().getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(e.getBlock().getLocation(), new SaveBlock(e.getBlock()));
            }
        }
    }
    
    @EventHandler
    public void onBlockLeavesDecay(LeavesDecayEvent e){
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        if(p == null){
            if(Changes.containsKey(e.getBlock().getLocation())){
                Changes.get(e.getBlock().getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(e.getBlock().getLocation(), new SaveBlock(e.getBlock()));
            }
        }
    }
    
    @EventHandler
    public void onBlockForm(BlockFormEvent e){
        Plot p = Plot.inPlot(e.getBlock().getLocation());
        if(p == null){
            if(Changes.containsKey(e.getBlock().getLocation())){
                Changes.get(e.getBlock().getLocation()).setBreakDate(new Date(System.currentTimeMillis()));
            }else{
                Changes.put(e.getBlock().getLocation(), new SaveBlock(e.getBlock()));
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
