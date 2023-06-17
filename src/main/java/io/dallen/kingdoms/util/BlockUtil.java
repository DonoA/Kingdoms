package io.dallen.kingdoms.util;

import org.bukkit.Material;

public class BlockUtil {
    
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
//    public static int rotate90(Material type, int data) {
//        switch (type) {
//            case TORCH:
//            case REDSTONE_TORCH_OFF:
//            case REDSTONE_TORCH_ON:
//                switch (data) {
//                    case 1:
//                        return 3;
//                    case 2:
//                        return 4;
//                    case 3:
//                        return 2;
//                    case 4:
//                        return 1;
//                }
//                break;
//
//            case RAILS:
//                switch (data) {
//                    case 6:
//                        return 7;
//                    case 7:
//                        return 8;
//                    case 8:
//                        return 9;
//                    case 9:
//                        return 6;
//                }
//            /* FALL-THROUGH */
//
//            case POWERED_RAIL:
//            case DETECTOR_RAIL:
//            case ACTIVATOR_RAIL:
//                switch (data & 0x7) {
//                    case 0:
//                        return 1 | (data & ~0x7);
//                    case 1:
//                        return 0 | (data & ~0x7);
//                    case 2:
//                        return 5 | (data & ~0x7);
//                    case 3:
//                        return 4 | (data & ~0x7);
//                    case 4:
//                        return 2 | (data & ~0x7);
//                    case 5:
//                        return 3 | (data & ~0x7);
//                }
//                break;
//
//            case WOOD_STAIRS:
//            case COBBLESTONE_STAIRS:
//            case BRICK_STAIRS:
//            case SMOOTH_STAIRS:
//            case NETHER_BRICK_STAIRS:
//            case SANDSTONE_STAIRS:
//            case SPRUCE_WOOD_STAIRS:
//            case BIRCH_WOOD_STAIRS:
//            case JUNGLE_WOOD_STAIRS:
//            case QUARTZ_STAIRS:
//            case ACACIA_STAIRS:
//            case DARK_OAK_STAIRS:
//                switch (data) {
//                    case 0:
//                        return 2;
//                    case 1:
//                        return 3;
//                    case 2:
//                        return 1;
//                    case 3:
//                        return 0;
//                    case 4:
//                        return 6;
//                    case 5:
//                        return 7;
//                    case 6:
//                        return 5;
//                    case 7:
//                        return 4;
//                }
//                break;
//
//            case STONE_BUTTON:
//            case WOOD_BUTTON: {
//                int thrown = data & 0x8;
//                switch (data & ~0x8) {
//                    case 1:
//                        return 3 | thrown;
//                    case 2:
//                        return 4 | thrown;
//                    case 3:
//                        return 2 | thrown;
//                    case 4:
//                        return 1 | thrown;
//                    // 0 and 5 are vertical
//                }
//                break;
//            }
//
//            case LEVER: {
//                int thrown = data & 0x8;
//                switch (data & ~0x8) {
//                    case 1:
//                        return 3 | thrown;
//                    case 2:
//                        return 4 | thrown;
//                    case 3:
//                        return 2 | thrown;
//                    case 4:
//                        return 1 | thrown;
//                    case 5:
//                        return 6 | thrown;
//                    case 6:
//                        return 5 | thrown;
//                    case 7:
//                        return 0 | thrown;
//                    case 0:
//                        return 7 | thrown;
//                }
//                break;
//            }
//
//            case WOODEN_DOOR:
//            case IRON_DOOR:
//                if ((data & 0x8) != 0) {
//                    // door top halves contain no orientation information
//                    break;
//                }
//
//            /* FALL-THROUGH */
//            case COCOA:
//            case TRIPWIRE_HOOK: {
//                int extra = data & ~0x3;
//                int withoutFlags = data & 0x3;
//                switch (withoutFlags) {
//                    case 0:
//                        return 1 | extra;
//                    case 1:
//                        return 2 | extra;
//                    case 2:
//                        return 3 | extra;
//                    case 3:
//                        return 0 | extra;
//                }
//                break;
//            }
//            case SIGN_POST:
//                return (data + 4) % 16;
//
//            case LADDER:
//            case WALL_SIGN:
//            case CHEST:
//            case FURNACE:
//            case BURNING_FURNACE:
//            case ENDER_CHEST:
//            case TRAPPED_CHEST:
//            case HOPPER: {
//                int extra = data & 0x8;
//                int withoutFlags = data & ~0x8;
//                switch (withoutFlags) {
//                    case 2:
//                        return 5 | extra;
//                    case 3:
//                        return 4 | extra;
//                    case 4:
//                        return 2 | extra;
//                    case 5:
//                        return 3 | extra;
//                }
//                break;
//            }
//            case DISPENSER:
//            case DROPPER:
//                int dispPower = data & 0x8;
//                switch (data & ~0x8) {
//                    case 2:
//                        return 5 | dispPower;
//                    case 3:
//                        return 4 | dispPower;
//                    case 4:
//                        return 2 | dispPower;
//                    case 5:
//                        return 3 | dispPower;
//                }
//                break;
//
//            case PUMPKIN:
//            case JACK_O_LANTERN:
//                switch (data) {
//                    case 0:
//                        return 1;
//                    case 1:
//                        return 2;
//                    case 2:
//                        return 3;
//                    case 3:
//                        return 0;
//                }
//                break;
//
//            case HAY_BLOCK:
//            case LOG:
//            case LOG_2:
//                if (data >= 4 && data <= 11) {
//                    data ^= 0xc;
//                }
//                break;
//
//            case REDSTONE_COMPARATOR:
//            case REDSTONE_COMPARATOR_OFF:
//            case REDSTONE_COMPARATOR_ON:
//            case DIODE:
//            case DIODE_BLOCK_OFF:
//            case DIODE_BLOCK_ON:
//                int dir = data & 0x03;
//                int delay = data - dir;
//                switch (dir) {
//                    case 0:
//                        return 1 | delay;
//                    case 1:
//                        return 2 | delay;
//                    case 2:
//                        return 3 | delay;
//                    case 3:
//                        return 0 | delay;
//                }
//                break;
//
//            case TRAP_DOOR:
//            case IRON_TRAPDOOR:
//                int withoutOrientation = data & ~0x3;
//                int orientation = data & 0x3;
//                switch (orientation) {
//                    case 0:
//                        return 3 | withoutOrientation;
//                    case 1:
//                        return 2 | withoutOrientation;
//                    case 2:
//                        return 0 | withoutOrientation;
//                    case 3:
//                        return 1 | withoutOrientation;
//                }
//                break;
//
//            case PISTON_BASE:
//            case PISTON_STICKY_BASE:
//            case PISTON_EXTENSION:
//                final int rest = data & ~0x7;
//                switch (data & 0x7) {
//                    case 2:
//                        return 5 | rest;
//                    case 3:
//                        return 4 | rest;
//                    case 4:
//                        return 2 | rest;
//                    case 5:
//                        return 3 | rest;
//                }
//                break;
//
//            case BROWN_MUSHROOM:
//            case RED_MUSHROOM:
//                if (data >= 10) {
//                    return data;
//                }
//                return (data * 3) % 10;
//
//            case VINE:
//                return ((data << 1) | (data >> 3)) & 0xf;
//
//            case FENCE_GATE:
//                return ((data + 1) & 0x3) | (data & ~0x3);
//
//            case ANVIL:
//                int damage = data & ~0x3;
//                switch (data & 0x3) {
//                    case 0:
//                        return 3 | damage;
//                    case 2:
//                        return 1 | damage;
//                    case 1:
//                        return 0 | damage;
//                    case 3:
//                        return 2 | damage;
//                }
//                break;
//
//            case BED:
//                return data & ~0x3 | (data + 1) & 0x3;
//
//            case SKULL:
//                switch (data) {
//                    case 2:
//                        return 5;
//                    case 3:
//                        return 4;
//                    case 4:
//                        return 2;
//                    case 5:
//                        return 3;
//                }
//        }
//
//        return data;
//    }

    /**
     * Rotate a block's data value -90 degrees (north<-east<-south<-west<-north);
     *
     * @param type the type ID of the bock
     * @param data the data ID of the block
     * @return the new data value
     */
//    public static int rotate90Reverse(Material type, int data) {
//        // case ([0-9]+): return ([0-9]+) -> case \2: return \1
//
//        switch (type) {
//            case TORCH:
//            case REDSTONE_TORCH_OFF:
//            case REDSTONE_TORCH_ON:
//                switch (data) {
//                    case 3:
//                        return 1;
//                    case 4:
//                        return 2;
//                    case 2:
//                        return 3;
//                    case 1:
//                        return 4;
//                }
//                break;
//
//            case RAILS:
//                switch (data) {
//                    case 7:
//                        return 6;
//                    case 8:
//                        return 7;
//                    case 9:
//                        return 8;
//                    case 6:
//                        return 9;
//                }
//            /* FALL-THROUGH */
//
//            case POWERED_RAIL:
//            case DETECTOR_RAIL:
//            case ACTIVATOR_RAIL:
//                int power = data & ~0x7;
//                switch (data & 0x7) {
//                    case 1:
//                        return 0 | power;
//                    case 0:
//                        return 1 | power;
//                    case 5:
//                        return 2 | power;
//                    case 4:
//                        return 3 | power;
//                    case 2:
//                        return 4 | power;
//                    case 3:
//                        return 5 | power;
//                }
//                break;
//
//            case WOOD_STAIRS:
//            case COBBLESTONE_STAIRS:
//            case BRICK_STAIRS:
//            case SMOOTH_STAIRS:
//            case NETHER_BRICK_STAIRS:
//            case SANDSTONE_STAIRS:
//            case SPRUCE_WOOD_STAIRS:
//            case BIRCH_WOOD_STAIRS:
//            case JUNGLE_WOOD_STAIRS:
//            case QUARTZ_STAIRS:
//            case ACACIA_STAIRS:
//            case DARK_OAK_STAIRS:
//                switch (data) {
//                    case 2:
//                        return 0;
//                    case 3:
//                        return 1;
//                    case 1:
//                        return 2;
//                    case 0:
//                        return 3;
//                    case 6:
//                        return 4;
//                    case 7:
//                        return 5;
//                    case 5:
//                        return 6;
//                    case 4:
//                        return 7;
//                }
//                break;
//
//            case STONE_BUTTON:
//            case WOOD_BUTTON: {
//                int thrown = data & 0x8;
//                switch (data & ~0x8) {
//                    case 3:
//                        return 1 | thrown;
//                    case 4:
//                        return 2 | thrown;
//                    case 2:
//                        return 3 | thrown;
//                    case 1:
//                        return 4 | thrown;
//                    // 0 and 5 are vertical
//                }
//                break;
//            }
//
//            case LEVER: {
//                int thrown = data & 0x8;
//                switch (data & ~0x8) {
//                    case 3:
//                        return 1 | thrown;
//                    case 4:
//                        return 2 | thrown;
//                    case 2:
//                        return 3 | thrown;
//                    case 1:
//                        return 4 | thrown;
//                    case 6:
//                        return 5 | thrown;
//                    case 5:
//                        return 6 | thrown;
//                    case 0:
//                        return 7 | thrown;
//                    case 7:
//                        return 0 | thrown;
//                }
//                break;
//            }
//
//            case WOODEN_DOOR:
//            case IRON_DOOR:
//                if ((data & 0x8) != 0) {
//                    // door top halves contain no orientation information
//                    break;
//                }
//
//            /* FALL-THROUGH */
//            case COCOA:
//            case TRIPWIRE_HOOK: {
//                int extra = data & ~0x3;
//                int withoutFlags = data & 0x3;
//                switch (withoutFlags) {
//                    case 1:
//                        return 0 | extra;
//                    case 2:
//                        return 1 | extra;
//                    case 3:
//                        return 2 | extra;
//                    case 0:
//                        return 3 | extra;
//                }
//                break;
//            }
//            case SIGN_POST:
//                return (data + 12) % 16;
//
//            case LADDER:
//            case WALL_SIGN:
//            case CHEST:
//            case FURNACE:
//            case BURNING_FURNACE:
//            case ENDER_CHEST:
//            case TRAPPED_CHEST:
//            case HOPPER: {
//                int extra = data & 0x8;
//                int withoutFlags = data & ~0x8;
//                switch (withoutFlags) {
//                    case 5:
//                        return 2 | extra;
//                    case 4:
//                        return 3 | extra;
//                    case 2:
//                        return 4 | extra;
//                    case 3:
//                        return 5 | extra;
//                }
//                break;
//            }
//            case DISPENSER:
//            case DROPPER:
//                int dispPower = data & 0x8;
//                switch (data & ~0x8) {
//                    case 5:
//                        return 2 | dispPower;
//                    case 4:
//                        return 3 | dispPower;
//                    case 2:
//                        return 4 | dispPower;
//                    case 3:
//                        return 5 | dispPower;
//                }
//                break;
//            case PUMPKIN:
//            case JACK_O_LANTERN:
//                switch (data) {
//                    case 1:
//                        return 0;
//                    case 2:
//                        return 1;
//                    case 3:
//                        return 2;
//                    case 0:
//                        return 3;
//                }
//                break;
//
//            case HAY_BLOCK:
//            case LOG:
//            case LOG_2:
//                if (data >= 4 && data <= 11) {
//                    data ^= 0xc;
//                }
//                break;
//
//            case REDSTONE_COMPARATOR:
//            case REDSTONE_COMPARATOR_OFF:
//            case REDSTONE_COMPARATOR_ON:
//            case DIODE:
//            case DIODE_BLOCK_OFF:
//            case DIODE_BLOCK_ON:
//                int dir = data & 0x03;
//                int delay = data - dir;
//                switch (dir) {
//                    case 1:
//                        return 0 | delay;
//                    case 2:
//                        return 1 | delay;
//                    case 3:
//                        return 2 | delay;
//                    case 0:
//                        return 3 | delay;
//                }
//                break;
//
//            case TRAP_DOOR:
//            case IRON_TRAPDOOR:
//                int withoutOrientation = data & ~0x3;
//                int orientation = data & 0x3;
//                switch (orientation) {
//                    case 3:
//                        return 0 | withoutOrientation;
//                    case 2:
//                        return 1 | withoutOrientation;
//                    case 0:
//                        return 2 | withoutOrientation;
//                    case 1:
//                        return 3 | withoutOrientation;
//                }
//
//            case PISTON_BASE:
//            case PISTON_STICKY_BASE:
//            case PISTON_EXTENSION:
//                final int rest = data & ~0x7;
//                switch (data & 0x7) {
//                    case 5:
//                        return 2 | rest;
//                    case 4:
//                        return 3 | rest;
//                    case 2:
//                        return 4 | rest;
//                    case 3:
//                        return 5 | rest;
//                }
//                break;
//
//            case BROWN_MUSHROOM:
//            case RED_MUSHROOM:
//                if (data >= 10) {
//                    return data;
//                }
//                return (data * 7) % 10;
//
//            case VINE:
//                return ((data >> 1) | (data << 3)) & 0xf;
//
//            case FENCE_GATE:
//                return ((data + 3) & 0x3) | (data & ~0x3);
//
//            case ANVIL:
//                int damage = data & ~0x3;
//                switch (data & 0x3) {
//                    case 0:
//                        return 1 | damage;
//                    case 2:
//                        return 3 | damage;
//                    case 1:
//                        return 2 | damage;
//                    case 3:
//                        return 0 | damage;
//                }
//                break;
//
//            case BED:
//                return data & ~0x3 | (data - 1) & 0x3;
//
//            case SKULL:
//                switch (data) {
//                    case 2:
//                        return 4;
//                    case 3:
//                        return 5;
//                    case 4:
//                        return 3;
//                    case 5:
//                        return 2;
//                }
//        }
//
//        return data;
//    }
}
