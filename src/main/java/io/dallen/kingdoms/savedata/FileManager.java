package io.dallen.kingdoms.savedata;

import io.dallen.kingdoms.Kingdoms;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {

    public static final String BLOCK_SAVE_DATA  = "customBlocks.json";
    public static final String KINGDOMS_SAVE_DATA  = "kingdoms.json";
    public static final String PLOT_SAVE_DATA  = "plots.json";

    public static void writeDataFile(String fileName, String toWrite) {
        try {
            createFolder();
            Files.writeString(Path.of("plugins", "kingdoms", fileName), toWrite);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to write data file " + fileName);
            e.printStackTrace();
        }
    }

    public static String readDataFile(String fileName) {
        try {
            createFolder();
            return Files.readString(Path.of("plugins", "kingdoms", fileName));
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to read data file " + fileName);
            e.printStackTrace();
        }
        return "";
    }

    public static void createFolder() throws IOException {
        var dataFolder = Path.of("plugins", "kingdoms");
        Files.createDirectories(dataFolder);
    }
}
