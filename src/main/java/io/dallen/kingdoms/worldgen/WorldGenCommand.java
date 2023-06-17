package io.dallen.kingdoms.worldgen;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.bukkit.util.noise.PerlinOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class WorldGenCommand implements CommandExecutor {

    KingdomsTerrain terrain = new KingdomsTerrain();

    WorldCreator creator  = new WorldCreator("kingdoms")
            .generator(new KingdomsChunkGen(terrain))
            .biomeProvider(new KingdomsBiomeGen(terrain));

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        commandSender.sendMessage("Deleting old world...");

        var player = (Player) commandSender;
        var defaultWorld = Bukkit.getWorld("world");
        player.teleport(defaultWorld.getSpawnLocation());

        Bukkit.unloadWorld(creator.name(), false);
        var oldWorldPath = Path.of(creator.name());
        if (Files.exists(oldWorldPath)) {
            Files.walk(oldWorldPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        commandSender.sendMessage("Building new world...");
        var kingdomsWorld = creator.createWorld();
        player.teleport(kingdomsWorld.getSpawnLocation());

        return true;
    }

    public static class KingdomsTerrain {
        public static final int base = 0;

        public static final double mountainCutOff = 0.65;
        public static final double valleyCutOff = 0.30;

        public static final double lakeScale = 100;
        public static final double valleyScale = 50;
        public static final double mountainScale = 550;

        public static final double maxHeightLake = base + (valleyCutOff * lakeScale);

        private double heightAtPoint(NoiseGenerator noise, int worldX, int worldZ) {
            var height = ((noise.noise((double) worldX/150,(double) worldZ/150)) + 1) / 2; // [0,1]

            if (height > mountainCutOff) { //Mountain
                return base + (valleyCutOff * lakeScale) +
                        ((mountainCutOff - valleyCutOff) * valleyScale) +
                        ((height - mountainCutOff) * mountainScale);
            } else if (height > valleyCutOff) { //valley
                return base + (valleyCutOff * lakeScale) +
                        ((height - valleyCutOff) * valleyScale);
            } else { // lake
                return base + (height * lakeScale);
            }
        }

        private Biome biomeAtPoint(NoiseGenerator noise, int worldX, int worldZ) {
            var height = ((noise.noise((double) worldX/150,(double) worldZ/150)) + 1) / 2; // [0,1]

            if (height > mountainCutOff) { //Mountain
                return Biome.WINDSWEPT_HILLS;
            } else if (height > valleyCutOff) { //valley
                return Biome.FOREST;
            } else { // lake
                return Biome.COLD_OCEAN;
            }
        }

        private List<Biome> getBiomes() {
            return List.of(
                    Biome.WINDSWEPT_HILLS,
                    Biome.FOREST,
                    Biome.COLD_OCEAN
            );
        }
    }

    @RequiredArgsConstructor
    public static class KingdomsChunkGen extends ChunkGenerator {

        private final KingdomsTerrain terrain;

        public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunk) {
            var noise = new PerlinNoiseGenerator(worldInfo.getSeed());

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    var trueX = x + chunkX * 16;
                    var trueZ = z + chunkZ * 16;
                    for (int y = chunk.getMinHeight() + 1; y < terrain.heightAtPoint(noise, trueX, trueZ); y++) {
                        switch (terrain.biomeAtPoint(noise, trueX, trueZ)) {
                            case WINDSWEPT_HILLS:
                                chunk.setBlock(x, y, z, Material.STONE);
                                break;
                            case FOREST:
                                chunk.setBlock(x, y, z, Material.DIRT);
                                break;
                            case COLD_OCEAN:
                                chunk.setBlock(x, y, z, Material.GRAVEL);
                                break;
                        }
                    }
                }
            }
        }

        public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunk) {
            var noise = new PerlinNoiseGenerator(worldInfo.getSeed());

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    var trueX = x + chunkX * 16;
                    var trueZ = z + chunkZ * 16;
                    var y = (int) terrain.heightAtPoint(noise, trueX, trueZ);
                    switch (terrain.biomeAtPoint(noise, trueX, trueZ)) {
                        case WINDSWEPT_HILLS:
                            chunk.setBlock(x, y, z, Material.STONE);
                            break;
                        case FOREST:
                            chunk.setBlock(x, y, z, Material.GRASS_BLOCK);
                            break;
                        case COLD_OCEAN:
                            for (int blockY = y; blockY < KingdomsTerrain.maxHeightLake + 1; blockY++) {
                                chunk.setBlock(x, blockY, z, Material.WATER);
                            }
                            break;
                    }
                    chunk.setBlock(x, 0, z, Material.GRASS_BLOCK);
                }
            }
        }

        public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunk) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunk.setBlock(x, chunk.getMinHeight(), z, Material.BEDROCK);
                }
            }
        }

        public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunk) {
            //pass
        }
    }

    @RequiredArgsConstructor
    public static class KingdomsBiomeGen extends BiomeProvider {
        private static final Biome defaultBiome = Biome.WINDSWEPT_HILLS;

        private final KingdomsTerrain terrain;

        @Override
        public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
            var noise = new PerlinNoiseGenerator(worldInfo.getSeed());

            return terrain.biomeAtPoint(noise, x, z);
        }

        @Override
        public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
            return terrain.getBiomes();
        }
    }
}
