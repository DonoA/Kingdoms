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

import lombok.Getter;
import org.bukkit.Material;

/**
 *
 * @author donoa_000
 */
public class Blueprint {
    private int len;
    private int wid;
    private int high;
    
    private BlueBlock[][][] blocks;
    
    public Blueprint(int l, int w, int h, byte[] b, byte[] d){
        this.len = l;
        this.wid = w;
        this.high = h;
        blocks = new BlueBlock[l][w][h];
        for(int i = 0; i < this.len; i++){
            for(int j = 0; j < this.wid; j++){
                for(int k = 0; k < this.high; k++){
                    blocks[i][j][k] = new BlueBlock(b[i+j+k], d[i+j+k]);
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
        
        public BlueBlock(byte b, byte d){
            Block = Material.getMaterial(b);
            Data = d;
        }
    }
}
