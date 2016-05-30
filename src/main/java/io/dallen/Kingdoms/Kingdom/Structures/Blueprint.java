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
    
    public void Rotate(int angle){//THIS does not account for stair blocks or other attached things atm
        BlueBlock[][][] ret = new BlueBlock[this.wid][this.high][this.len];
        if(angle == 90){
            final int M = blocks.length;
            final int N = blocks[0][0].length;
            printMatrix(blocks);
            ret = new BlueBlock[N][this.high][M];
            for (int r = 0; r < M; r++) {
                for (int c = 0; c < N; c++) {
                    for(int y = 0; y < this.high; y++){
                        ret[c][y][M-1-r] = blocks[r][y][c];
                    }
                }
            }
            printMatrix(ret);
        }else if(angle == -90){//I really hope these work, they are hot off StackOverflow
            final int M = blocks.length;
            final int N = blocks[0][0].length;
            printMatrix(blocks);
            ret = new BlueBlock[N][this.high][M];
            for (int r = 0; r < M; r++) {
                for (int c = 0; c < N; c++) {
                    for(int y = 0; y < this.high; y++){
                        ret[N-1-c][y][r] = blocks[r][y][c];
                    }
                }
            }
            printMatrix(ret);
        }
        this.blocks = ret;
        this.wid = ret.length;
        this.high = ret[0].length;
        this.len = ret[0][0].length;
    }
    
    static void printMatrix(BlueBlock[][][] mat) {
        System.out.println("Matrix = ");
        for (BlueBlock[][] row : mat) {
            System.out.println(Arrays.toString(row[0]));
        }
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
        
        @Override
        public String toString(){
            return String.valueOf(Block.getId());
        }
    }
}
