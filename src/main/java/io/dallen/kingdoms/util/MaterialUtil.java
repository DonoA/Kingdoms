package io.dallen.kingdoms.util;

import org.bukkit.Material;

public class MaterialUtil {

    public static boolean isBed(Material m) {
        switch (m) {
            case WHITE_BED:
            case BLACK_BED:
            case LIGHT_GRAY_BED:
            case GRAY_BED:
            case BROWN_BED:
            case RED_BED:
            case ORANGE_BED:
            case YELLOW_BED:
            case LIME_BED:
            case GREEN_BED:
            case CYAN_BED:
            case LIGHT_BLUE_BED:
            case BLUE_BED:
            case PURPLE_BED:
            case MAGENTA_BED:
            case PINK_BED:
                return true;
            default:
                return false;
        }
    }

    public static boolean isChest(Material m) {
        switch (m) {
            case CHEST:
            case TRAPPED_CHEST:
                return true;
            default:
                return false;
        }
    }

}
