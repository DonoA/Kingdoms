package io.dallen.kingdoms.customblocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.savedata.SaveData;
import io.dallen.kingdoms.savedata.bukkit.JsonLocation;
import io.dallen.kingdoms.savedata.FileManager;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomBlockData implements SaveData<CustomBlockData> {
    private static Map<Location, CustomBlockData> blockDataMap = new HashMap<>();
    private static Map<String, Class<?>> subclasses = new HashMap<>();
    public static <T extends CustomBlockData> void registerSubclass(Class<T> clss) {
        subclasses.put(clss.getCanonicalName(), clss);
    }

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

    public static void saveAll() {
        var blockDatas = new JsonArray();
        for (var entry : blockDataMap.entrySet()) {
            var typeName = entry.getValue().getClass().getCanonicalName();
            var dataAsJson = Kingdoms.gson.toJson(entry.getValue());
            var blockLoc = new JsonLocation(entry.getKey());

            var blockData = new JsonObject();
            blockData.add("location", Kingdoms.gson.toJsonTree(blockLoc));
            blockData.addProperty("type", typeName);
            blockData.addProperty("data", dataAsJson);

            blockDatas.add(blockData);
        }
        var dataJson = Kingdoms.gson.toJson(blockDatas);
        FileManager.writeDataFile(FileManager.BLOCK_SAVE_DATA, dataJson);
    }

    public static void loadAll() {

    }

}
