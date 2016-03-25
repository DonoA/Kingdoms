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
package io.dallen.Kingdoms.Kingdom.Structures;

import io.dallen.Kingdoms.Util.LogUtil;
import java.util.Arrays;
import lombok.Getter;
import org.bukkit.Material;

/**
 *
 * @author donoa_000
 */
public class Blueprint {
    @Getter
    private int len;
    @Getter
    private int wid;
    @Getter
    private int high;
    
    @Getter
    private BlueBlock[][][] blocks; //X Y Z
    
    public Blueprint(int l, int w, int h, short[] b, byte[] d){
        LogUtil.printDebug(Arrays.toString(b));
        this.len = l;
        this.wid = w;
        this.high = h;
        blocks = new BlueBlock[w][h][l];
        int currentOffset = 0;
        for(int y = 0; y < this.high; y++){
            for(int z = 0; z < this.len; z++){
                for(int x = 0; x < this.wid; x++){
                    blocks[x][y][z] = new BlueBlock(b[currentOffset], d[currentOffset]);
                    currentOffset++;
                }
            }
        }
    }
    
    public Blueprint Rotate(int angle){
        return this;
    }
    
    public Blueprint replace(BlueBlock find, BlueBlock replace){
        return this;
    }
    
    
    
    public static class BlueBlock{
        @Getter
        private Material Block;
        @Getter
        private byte Data;
        
        public BlueBlock(short b, byte d){
            Block = Material.getMaterial(b);
            Data = d;
        }
    }
}
