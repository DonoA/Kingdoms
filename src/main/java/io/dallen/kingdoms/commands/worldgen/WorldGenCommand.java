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
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
public class WorldGenCommand implements CommandExecutor {

    private final World overworld;

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        commandSender.sendMessage("Deleting old world...");

        var player = (Player) commandSender;
        player.teleport(overworld.getSpawnLocation());

        var tempWorldName = "temp";

        // Delete temp world if existing
        Bukkit.unloadWorld(tempWorldName, false);
        var oldWorldPath = Path.of(tempWorldName);
        if (Files.exists(oldWorldPath)) {
            Files.walk(oldWorldPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }

        if (strings.length > 0) {
            return true;
        }

        // Generate new temp world for player while data is updated
        commandSender.sendMessage("Building new world...");
        var tempCreator = new WorldCreator(tempWorldName)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT);
        var tempWorld = tempCreator.createWorld();
        player.teleport(tempWorld.getSpawnLocation());

        Bukkit.unloadWorld(overworld, true);

        var defaultWorldName = overworld.getName();
        var worldDatapacks = Path.of(defaultWorldName, "datapacks");
        Files.createDirectories(worldDatapacks);
        Files.copy(Path.of("plugins", "kingdoms-datapack.zip"),
                Path.of(worldDatapacks.toString(), "kingdoms-datapack.zip"),
                StandardCopyOption.REPLACE_EXISTING);

        var regionFolder = Path.of(defaultWorldName, "region");
        Files.walk(regionFolder)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        var reloadOverworld = Bukkit.createWorld(new WorldCreator(defaultWorldName));
        player.teleport(reloadOverworld.getSpawnLocation());
        Bukkit.unloadWorld(tempWorld, false);

        player.kickPlayer("Go unzip the datapack");

        Bukkit.shutdown();
        return true;
    }
}