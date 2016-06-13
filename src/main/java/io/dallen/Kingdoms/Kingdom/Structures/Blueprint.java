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
import java.awt.Point;
import java.util.Arrays;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class Blueprint {
    @Getter
    private int len; // Z
    @Getter
    private int wid; // X
    @Getter
    private int high; // Y
    
    @Getter
    private Point offSet;
    
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
        offSet = new Point(0, 0);
    }
    
    public static enum buildType{
        CLEAR, FRAME;
    }
    
    public void build(Location start, buildType type){
        for(int y = 0; y < high; y++){
            for(int z = 0; z < len; z++){
                for(int x = 0; x < wid; x++){
                    Location nLoc = start.clone().add(x,y,z);
                    if(type.equals(buildType.CLEAR)){
                        nLoc.getBlock().setType(Material.AIR, false);
                    }else if(type.equals(buildType.FRAME)){
                        Material covMat = blocks[x][y][z].getBlock();
                        if(covMat.name().contains("STAIRS")){
                            covMat = Material.QUARTZ_STAIRS;
                        }else if(!covMat.equals(Material.AIR)){
                            covMat = Material.QUARTZ_BLOCK;
                        }
                        nLoc.getBlock().setType(covMat, false);
                        nLoc.getBlock().setData(blocks[x][y][z].getData(), false);
                    }
                }
            }
        }
    }
        
    public void fakeBuild(Location start, buildType type, Player p){
        for(int y = 0; y < high; y++){
            for(int z = 0; z < len; z++){
                for(int x = 0; x < wid; x++){
                    Location nLoc = start.clone().add(x,y,z);
                    if(type.equals(buildType.CLEAR)){
                        nLoc.getBlock().setType(Material.AIR, false);
                    }else if(type.equals(buildType.FRAME)){
                        Material covMat = blocks[x][y][z].getBlock();
                        if(covMat.name().contains("STAIRS")){
                            covMat = Material.QUARTZ_STAIRS;
                        }else if(!covMat.equals(Material.AIR)){
                            covMat = Material.QUARTZ_BLOCK;
                        }
                        nLoc.getBlock().setType(covMat, false);
                        nLoc.getBlock().setData(blocks[x][y][z].getData(), false);
                    }
                }
            }
        }
    }
    
    public void Rotate(int angle){
        BlueBlock[][][] ret = new BlueBlock[this.wid][this.high][this.len];
        if(angle == 90){
            final int M = blocks.length;
            final int N = blocks[0][0].length;
            printMatrix(blocks);
            ret = new BlueBlock[N][this.high][M];
            for (int r = 0; r < M; r++) {
                for (int c = 0; c < N; c++) {
                    for(int y = 0; y < this.high; y++){
                        blocks[r][y][c].Data = (byte) Blueprint.rotate90(blocks[r][y][c].getBlock(), (int) blocks[r][y][c].getData());
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
                        blocks[r][y][c].Data = (byte) Blueprint.rotate90(blocks[r][y][c].getBlock(), (int) blocks[r][y][c].getData());
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
        LogUtil.printDebug("Matrix = ");
        for (BlueBlock[][] row : mat) {
            LogUtil.printDebug(Arrays.toString(row[0]));
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
    
    /**
     * The following is based on code by sk89q
     * 
     */
    
    /**
     * Rotate a block's data value 90 degrees (north->east->south->west->north);
     * 
     * @param type the type ID of the bock
     * @param data the data ID of the block
     * @return the new data value
     */
    public static int rotate90(Material type, int data) {
        switch (type) {
        case TORCH:
        case REDSTONE_TORCH_OFF:
        case REDSTONE_TORCH_ON:
            switch (data) {
            case 1: return 3;
            case 2: return 4;
            case 3: return 2;
            case 4: return 1;
            }
            break;

        case RAILS:
            switch (data) {
            case 6: return 7;
            case 7: return 8;
            case 8: return 9;
            case 9: return 6;
            }
            /* FALL-THROUGH */

        case POWERED_RAIL:
        case DETECTOR_RAIL:
        case ACTIVATOR_RAIL:
            switch (data & 0x7) {
            case 0: return 1 | (data & ~0x7);
            case 1: return 0 | (data & ~0x7);
            case 2: return 5 | (data & ~0x7);
            case 3: return 4 | (data & ~0x7);
            case 4: return 2 | (data & ~0x7);
            case 5: return 3 | (data & ~0x7);
            }
            break;
            
        case WOOD_STAIRS:
        case COBBLESTONE_STAIRS:
        case BRICK_STAIRS:
        case SMOOTH_STAIRS:
        case NETHER_BRICK_STAIRS:
        case SANDSTONE_STAIRS:
        case SPRUCE_WOOD_STAIRS:
        case BIRCH_WOOD_STAIRS:
        case JUNGLE_WOOD_STAIRS:
        case QUARTZ_STAIRS:
        case ACACIA_STAIRS:
        case DARK_OAK_STAIRS:
            switch (data) {
            case 0: return 2;
            case 1: return 3;
            case 2: return 1;
            case 3: return 0;
            case 4: return 6;
            case 5: return 7;
            case 6: return 5;
            case 7: return 4;
            }
            break;

        case STONE_BUTTON:
        case WOOD_BUTTON: {
            int thrown = data & 0x8;
            switch (data & ~0x8) {
            case 1: return 3 | thrown;
            case 2: return 4 | thrown;
            case 3: return 2 | thrown;
            case 4: return 1 | thrown;
            // 0 and 5 are vertical
            }
            break;
        }

        case LEVER: {
            int thrown = data & 0x8;
            switch (data & ~0x8) {
            case 1: return 3 | thrown;
            case 2: return 4 | thrown;
            case 3: return 2 | thrown;
            case 4: return 1 | thrown;
            case 5: return 6 | thrown;
            case 6: return 5 | thrown;
            case 7: return 0 | thrown;
            case 0: return 7 | thrown;
            }
            break;
        }

        case WOODEN_DOOR:
        case IRON_DOOR:
            if ((data & 0x8) != 0) {
                // door top halves contain no orientation information
                break;
            }

            /* FALL-THROUGH */

        case COCOA:
        case TRIPWIRE_HOOK: {
            int extra = data & ~0x3;
            int withoutFlags = data & 0x3;
            switch (withoutFlags) {
            case 0: return 1 | extra;
            case 1: return 2 | extra;
            case 2: return 3 | extra;
            case 3: return 0 | extra;
            }
            break;
        }
        case SIGN_POST:
            return (data + 4) % 16;

        case LADDER:
        case WALL_SIGN:
        case CHEST:
        case FURNACE:
        case BURNING_FURNACE:
        case ENDER_CHEST:
        case TRAPPED_CHEST:
        case HOPPER: {
            int extra = data & 0x8;
            int withoutFlags = data & ~0x8;
            switch (withoutFlags) {
            case 2: return 5 | extra;
            case 3: return 4 | extra;
            case 4: return 2 | extra;
            case 5: return 3 | extra;
            }
            break;
        }
        case DISPENSER:
        case DROPPER:
            int dispPower = data & 0x8;
            switch (data & ~0x8) {
            case 2: return 5 | dispPower;
            case 3: return 4 | dispPower;
            case 4: return 2 | dispPower;
            case 5: return 3 | dispPower;
            }
            break;

        case PUMPKIN:
        case JACK_O_LANTERN:
            switch (data) {
            case 0: return 1;
            case 1: return 2;
            case 2: return 3;
            case 3: return 0;
            }
            break;

        case HAY_BLOCK:
        case LOG:
        case LOG_2:
            if (data >= 4 && data <= 11) data ^= 0xc;
            break;
            
        case REDSTONE_COMPARATOR:
        case REDSTONE_COMPARATOR_OFF:
        case REDSTONE_COMPARATOR_ON:
        case DIODE:
        case DIODE_BLOCK_OFF:
        case DIODE_BLOCK_ON:
            int dir = data & 0x03;
            int delay = data - dir;
            switch (dir) {
            case 0: return 1 | delay;
            case 1: return 2 | delay;
            case 2: return 3 | delay;
            case 3: return 0 | delay;
            }
            break;

        case TRAP_DOOR:
        case IRON_TRAPDOOR:
            int withoutOrientation = data & ~0x3;
            int orientation = data & 0x3;
            switch (orientation) {
            case 0: return 3 | withoutOrientation;
            case 1: return 2 | withoutOrientation;
            case 2: return 0 | withoutOrientation;
            case 3: return 1 | withoutOrientation;
            }
            break;

        case PISTON_BASE:
        case PISTON_STICKY_BASE:
        case PISTON_EXTENSION:
            final int rest = data & ~0x7;
            switch (data & 0x7) {
            case 2: return 5 | rest;
            case 3: return 4 | rest;
            case 4: return 2 | rest;
            case 5: return 3 | rest;
            }
            break;

        case BROWN_MUSHROOM:
        case RED_MUSHROOM:
            if (data >= 10) return data;
            return (data * 3) % 10;

        case VINE:
            return ((data << 1) | (data >> 3)) & 0xf;

        case FENCE_GATE:
            return ((data + 1) & 0x3) | (data & ~0x3);

        case ANVIL:
            int damage = data & ~0x3;
            switch (data & 0x3) {
            case 0: return 3 | damage;
            case 2: return 1 | damage;
            case 1: return 0 | damage;
            case 3: return 2 | damage;
            }
            break;

        case BED:
            return data & ~0x3 | (data + 1) & 0x3;

        case SKULL:
            switch (data) {
                case 2: return 5;
                case 3: return 4;
                case 4: return 2;
                case 5: return 3;
            }
        }

        return data;
    }

    /**
     * Rotate a block's data value -90 degrees (north<-east<-south<-west<-north);
     *
     * @param type the type ID of the bock
     * @param data the data ID of the block
     * @return the new data value
     */
    public static int rotate90Reverse(Material type, int data) {
        // case ([0-9]+): return ([0-9]+) -> case \2: return \1

        switch (type) {
        case TORCH:
        case REDSTONE_TORCH_OFF:
        case REDSTONE_TORCH_ON:
            switch (data) {
            case 3: return 1;
            case 4: return 2;
            case 2: return 3;
            case 1: return 4;
            }
            break;

        case RAILS:
            switch (data) {
            case 7: return 6;
            case 8: return 7;
            case 9: return 8;
            case 6: return 9;
            }
            /* FALL-THROUGH */

        case POWERED_RAIL:
        case DETECTOR_RAIL:
        case ACTIVATOR_RAIL:
            int power = data & ~0x7;
            switch (data & 0x7) {
            case 1: return 0 | power;
            case 0: return 1 | power;
            case 5: return 2 | power;
            case 4: return 3 | power;
            case 2: return 4 | power;
            case 3: return 5 | power;
            }
            break;

        case WOOD_STAIRS:
        case COBBLESTONE_STAIRS:
        case BRICK_STAIRS:
        case SMOOTH_STAIRS:
        case NETHER_BRICK_STAIRS:
        case SANDSTONE_STAIRS:
        case SPRUCE_WOOD_STAIRS:
        case BIRCH_WOOD_STAIRS:
        case JUNGLE_WOOD_STAIRS:
        case QUARTZ_STAIRS:
        case ACACIA_STAIRS:
        case DARK_OAK_STAIRS:
            switch (data) {
            case 2: return 0;
            case 3: return 1;
            case 1: return 2;
            case 0: return 3;
            case 6: return 4;
            case 7: return 5;
            case 5: return 6;
            case 4: return 7;
            }
            break;

        case STONE_BUTTON:
        case WOOD_BUTTON: {
            int thrown = data & 0x8;
            switch (data & ~0x8) {
            case 3: return 1 | thrown;
            case 4: return 2 | thrown;
            case 2: return 3 | thrown;
            case 1: return 4 | thrown;
            // 0 and 5 are vertical
            }
            break;
        }

        case LEVER: {
            int thrown = data & 0x8;
            switch (data & ~0x8) {
            case 3: return 1 | thrown;
            case 4: return 2 | thrown;
            case 2: return 3 | thrown;
            case 1: return 4 | thrown;
            case 6: return 5 | thrown;
            case 5: return 6 | thrown;
            case 0: return 7 | thrown;
            case 7: return 0 | thrown;
            }
            break;
        }

        case WOODEN_DOOR:
        case IRON_DOOR:
            if ((data & 0x8) != 0) {
                // door top halves contain no orientation information
                break;
            }

            /* FALL-THROUGH */

        case COCOA:
        case TRIPWIRE_HOOK: {
            int extra = data & ~0x3;
            int withoutFlags = data & 0x3;
            switch (withoutFlags) {
            case 1: return 0 | extra;
            case 2: return 1 | extra;
            case 3: return 2 | extra;
            case 0: return 3 | extra;
            }
            break;
        }
        case SIGN_POST:
            return (data + 12) % 16;

        case LADDER:
        case WALL_SIGN:
        case CHEST:
        case FURNACE:
        case BURNING_FURNACE:
        case ENDER_CHEST:
        case TRAPPED_CHEST:
        case HOPPER: {
            int extra = data & 0x8;
            int withoutFlags = data & ~0x8;
            switch (withoutFlags) {
                case 5: return 2 | extra;
                case 4: return 3 | extra;
                case 2: return 4 | extra;
                case 3: return 5 | extra;
            }
            break;
        }
        case DISPENSER:
        case DROPPER:
            int dispPower = data & 0x8;
            switch (data & ~0x8) {
            case 5: return 2 | dispPower;
            case 4: return 3 | dispPower;
            case 2: return 4 | dispPower;
            case 3: return 5 | dispPower;
            }
            break;
        case PUMPKIN:
        case JACK_O_LANTERN:
            switch (data) {
            case 1: return 0;
            case 2: return 1;
            case 3: return 2;
            case 0: return 3;
            }
            break;

        case HAY_BLOCK:
        case LOG:
        case LOG_2:
            if (data >= 4 && data <= 11) data ^= 0xc;
            break;

        case REDSTONE_COMPARATOR:
        case REDSTONE_COMPARATOR_OFF:
        case REDSTONE_COMPARATOR_ON:
        case DIODE:
        case DIODE_BLOCK_OFF:
        case DIODE_BLOCK_ON:
            int dir = data & 0x03;
            int delay = data - dir;
            switch (dir) {
            case 1: return 0 | delay;
            case 2: return 1 | delay;
            case 3: return 2 | delay;
            case 0: return 3 | delay;
            }
            break;

        case TRAP_DOOR:
        case IRON_TRAPDOOR:
            int withoutOrientation = data & ~0x3;
            int orientation = data & 0x3;
            switch (orientation) {
            case 3: return 0 | withoutOrientation;
            case 2: return 1 | withoutOrientation;
            case 0: return 2 | withoutOrientation;
            case 1: return 3 | withoutOrientation;
            }

        case PISTON_BASE:
        case PISTON_STICKY_BASE:
        case PISTON_EXTENSION:
            final int rest = data & ~0x7;
            switch (data & 0x7) {
            case 5: return 2 | rest;
            case 4: return 3 | rest;
            case 2: return 4 | rest;
            case 3: return 5 | rest;
            }
            break;

        case BROWN_MUSHROOM:
        case RED_MUSHROOM:
            if (data >= 10) return data;
            return (data * 7) % 10;

        case VINE:
            return ((data >> 1) | (data << 3)) & 0xf;

        case FENCE_GATE:
            return ((data + 3) & 0x3) | (data & ~0x3);

        case ANVIL:
            int damage = data & ~0x3;
            switch (data & 0x3) {
            case 0: return 1 | damage;
            case 2: return 3 | damage;
            case 1: return 2 | damage;
            case 3: return 0 | damage;
            }
            break;

        case BED:
            return data & ~0x3 | (data - 1) & 0x3;

        case SKULL:
            switch (data) {
                case 2: return 4;
                case 3: return 5;
                case 4: return 3;
                case 5: return 2;
            }
        }

        return data;
    }
}
