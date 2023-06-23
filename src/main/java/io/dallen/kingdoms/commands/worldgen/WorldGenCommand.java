package io.dallen.kingdoms.commands.worldgen;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
public class WorldGenCommand implements CommandExecutor, Listener {

    private final World overworld;

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        commandSender.sendMessage("Deleting old world...");
        commandSender.sendMessage("New Version");

        var player = (Player) commandSender;
        player.teleport(overworld.getSpawnLocation());

        WorldCreator creator = new WorldCreator("kingdomszz")
                .environment(World.Environment.NORMAL)
                .type(WorldType.NORMAL);

        /*
        {
        "generate_features": false
          "dimensions": {
            "minecraft:overworld": {
              "type": "minecraft:overworld",
              "generator": {
                "biome_source": {
                  "seed": 0,
                  "large_biomes": false,
                  "type": "minecraft:vanilla_layered"
                },
                "seed": 0,
                "settings": "minecraft:overworld",
                "type": "minecraft:noise"
              }
            },
           }

         */

        Bukkit.unloadWorld(creator.name(), false);
        var oldWorldPath = Path.of(creator.name());
        if (Files.exists(oldWorldPath)) {
            Files.walk(oldWorldPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        if (strings.length > 0) {
            return true;
        }

        commandSender.sendMessage("Building new world...");
        var kingdomsWorld = creator.createWorld();
        player.teleport(kingdomsWorld.getSpawnLocation());

        return true;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (e.isNewChunk()) {
            System.out.println("New Chunk");
        }
    }

//    @RequiredArgsConstructor
//    public static class KingdomsChunkGen extends ChunkGenerator {
//
//        private final ChunkGenerator defaultGen;
//
//        @Override
//        public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
//            defaultGen.generateNoise(worldInfo, random, chunkX, chunkZ, chunkData);
//        }
//
//        @Override
//        public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
//            defaultGen.generateSurface(worldInfo, random, chunkX, chunkZ, chunkData);
//        }
//
//        @Override
//        public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
//            defaultGen.generateBedrock(worldInfo, random, chunkX, chunkZ, chunkData);
//        }
//
//        @Override
//        public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkGenerator.ChunkData chunkData) {
//            //pass
//        }
//    }
}
