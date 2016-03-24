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
