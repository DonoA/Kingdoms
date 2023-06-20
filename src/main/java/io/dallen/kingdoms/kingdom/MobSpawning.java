package io.dallen.kingdoms.kingdom;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MobSpawning {

    private final World overworld;
    private final Plugin plugin;

    private int spawnRadius = 15;

    private int maxMonsters = 10;

    private ConcurrentLinkedQueue<Location> spawnLocations = new ConcurrentLinkedQueue<>();

    public static void startSpawning(Plugin plugin, World overworld) {
        var spawning = new MobSpawning(overworld, plugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, spawning::locateSpawns, 20, 20);
    }

    private void doSpawns(Kingdom kingdom, Iterator<Location> iter) {
        for (int i = kingdom.getAttackers().size(); i < maxMonsters; i++) {
            if (!iter.hasNext()) {
                return;
            }

            var targetLoc = iter.next();
            Entity zombie = kingdom.getClaim().getWorld().spawnEntity(targetLoc.add(0, 1, 0), EntityType.ZOMBIE);
            // Set target as a the claim block. Attempt to locate the weakest point in the defences
            kingdom.getAttackers().put(zombie.getEntityId(), zombie);
        }
    }

    public void locateSpawns() {
        for (var kingdom : Kingdom.getAllKingdoms().values()) {
            spawnAttackers(kingdom);
        }
    }

    private void spawnAttackers(Kingdom kingdom) {
        var spawnLoc = new ArrayList<Location>();

        kingdom.getBounds()
                .clone()
                .add(spawnRadius, spawnRadius, spawnRadius, spawnRadius)
                .forEach((x, z, i) -> {
                    if (kingdom.getBounds().contains(x, z)) {
                        return;
                    }

                    if (kingdom.getAttackers().size() >= maxMonsters) {
                        return;
                    }

                    Location spawn = trySpawn(kingdom, x, z);
                    if (spawn == null) {
                        return;
                    }

                    spawnLoc.add(spawn);
                });

        Collections.shuffle(spawnLoc);
        Bukkit.getScheduler().runTask(plugin, () -> doSpawns(kingdom, spawnLoc.iterator()));
    }

    private Location trySpawn(Kingdom k, int x, int z) {
        for (int y = 0; y < spawnRadius; y++) {
            var testLoc = new Location(k.getClaim().getWorld(), x, k.getClaim().getBlockY() + y, z);
            if (canSpawn(testLoc)) {
                return testLoc;
            }
        }

        for (int y = 0; y < spawnRadius; y++) {
            var testLoc = new Location(k.getClaim().getWorld(), x, k.getClaim().getBlockY() - y, z);
            if (canSpawn(testLoc)) {
                return testLoc;
            }
        }

        return null;
    }

    private boolean canSpawn(Location l) {
        return l.getBlock().getType().isSolid() &&
                l.clone().add(0, 1, 0).getBlock().getType() == Material.AIR &&
                l.clone().add(0, 2, 0).getBlock().getType() == Material.AIR;
    }

}