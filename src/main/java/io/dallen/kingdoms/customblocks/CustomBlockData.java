package io.dallen.kingdoms.customblocks;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class CustomBlockData {

    private static Map<Location, CustomBlockData> blockDataMap = new HashMap<>();

    public static <T extends CustomBlockData> T getBlockData(Location l, Class<T> clss) {
        var data = blockDataMap.get(l);
        if (clss.isInstance(data)) {
            return (T) data;
        }
        return null;
    }

    public static <T extends CustomBlockData> void setBlockData(Location l, T data) {
        blockDataMap.put(l, data);
    }
    public static <T extends CustomBlockData> T removeBlockData(Location l, Class<T> clss) {
        var data = blockDataMap.remove(l);
        if (clss.isInstance(data)) {
            return (T) data;
        }
        return null;
    }

}
