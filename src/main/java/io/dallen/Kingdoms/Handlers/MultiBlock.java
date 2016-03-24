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
package io.dallen.Kingdoms.Handlers;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
