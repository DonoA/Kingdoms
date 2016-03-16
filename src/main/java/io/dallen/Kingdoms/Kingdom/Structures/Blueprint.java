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
