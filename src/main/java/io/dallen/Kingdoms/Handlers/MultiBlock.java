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
package io.dallen.Kingdoms.Handlers;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;

/**
 *
 * @author donoa_000
 */
public class MultiBlock implements Listener{
    
    private String type;
    private Location center;
    private int len; // Z
    private int wid; // X
    private int height; // Y
    
    private static HashMap<Location, MultiBlock> MultiBlocks = new HashMap<Location, MultiBlock>();
    
    public MultiBlock(Location cent, int l, int w, int h){
        this.center = cent;
        this.len = l;
        this.wid = w;
        this.height = h;
    }
    
    public static void checkMultiBlock(BlockEvent e, Block b){
        if(MultiBlocks.containsKey(b.getLocation())){
            MultiBlocks.get(b.getLocation()).destroy();
        }
    }
    
    public void destroy(){
        for(int X = center.getBlockX() - wid/2; X < center.getBlockX() + wid/2; X++){
            for(int Z = center.getBlockZ() - len/2; Z < center.getBlockZ() + len/2; Z++){
                for(int Y = center.getBlockY() - height/2; Y < center.getBlockY() + height/2; Y++){
                    MultiBlocks.remove(new Location(center.getWorld(), X, Y, Z));
                }
            }
        }
    }
    
}
