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

import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 *
 * @author donoa_000
 */
public class BiomeGenHandler implements Listener{
    
    @EventHandler
    public void onChunckGenerate(ChunkLoadEvent e){
        if(e.isNewChunk()){
            ChunkSnapshot chunk = e.getChunk().getChunkSnapshot();
            for(int X = 0; X <=15; X++){
                for(int Z = 0; Z <= 15; Z++){
                    if(chunk.getBiome(X, Z).equals(Biome.PLAINS) && Math.random() <= .1){
                        boolean running = true;
                        for(int Y = 127; Y >= 0 && running; Y--){
                            if(e.getChunk().getBlock(X, Y, Z).getType().equals(Material.GRASS)){
                               running = false;
                               e.getChunk().getBlock(X, Y, Z).setType(Material.SOIL);
                               e.getChunk().getBlock(X, Y + 1, Z).setType(Material.WHEAT);
                            }
                        }
                    }
                }
            }
        }
    }
}
