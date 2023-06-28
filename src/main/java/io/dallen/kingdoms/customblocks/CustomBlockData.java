package io.dallen.kingdoms.customblocks;

import io.dallen.kingdoms.savedata.FileManager;
import io.dallen.kingdoms.savedata.SaveDataManager;
import lombok.Getter;
import org.bukkit.Location;

public abstract class CustomBlockData {
    @Getter
    private static SaveDataManager<Location, CustomBlockData> blockData =
            new SaveDataManager<>(FileManager.BLOCK_SAVE_DATA, Location.class);

    public static <T extends CustomBlockData> T getBlockData(Location l, Class<T> clss) {
        var data = blockData.get(l);
        if (clss.isInstance(data)) {
            return (T) data;
        }
        return null;
    }

    public static <T extends CustomBlockData> void setBlockData(Location l, T data) {
        blockData.put(l, data);
    }
    public static <T extends CustomBlockData> T removeBlockData(Location l, Class<T> clss) {
        var data = blockData.remove(l);
        if (clss.isInstance(data)) {
            return (T) data;
        }
        return null;
    }
}
