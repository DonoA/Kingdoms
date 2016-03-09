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
package io.dallen.Kingdoms.Terrain;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author donoa_000
 */
public class KingdomChunkGenerator extends ChunkGenerator {
    //This is where you stick your populators - these will be covered in another tutorial
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList((BlockPopulator) new OverworldPopulator());
    }
    //This needs to be set to return true to override minecraft's default behaviour
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }
    //This converts relative chunk locations to bytes that can be written to the chunk
    public int xyzToByte(int x, int y, int z) {
        return (x * 16 + z) * 128 + y;
    }
 
    @Override
    public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
        byte[] result = new byte[32768];
        int y = 0;
        //This will set the floor of each chunk at bedrock level to bedrock
        for(int x=0; x<16; x++){
            for(int z=0; z<16; z++) {
                result[xyzToByte(x,y,z)] = (byte) Material.BEDROCK.getId();
            }
        }
        return result;
    }
 
}
