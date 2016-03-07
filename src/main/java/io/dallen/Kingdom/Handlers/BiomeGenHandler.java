/*
 * This file is part of Kingdom.
 * 
 * Kingdom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdom.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdom.Handlers;

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
