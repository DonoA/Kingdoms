package io.dallen.kingdoms.kingdom.mobs;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.kingdom.ai.TargetClaimAI;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MobSpawning {

    private final Plugin plugin;

    private int spawnRadius = 15;

    private int maxMonsters = 0;

    public static BukkitTask startSpawning(Plugin plugin) {
        var spawning = new MobSpawning(plugin);
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, spawning::locateSpawns, 20, 20);
    }

    private void doSpawns(Kingdom kingdom, Iterator<Location> iter) {
        var worldTime = kingdom.getBlock().getWorld().getTime();
        if (worldTime > 0 && worldTime < 12_000) {
            return;
        }

        for (int i = kingdom.getAttackers().size(); i < maxMonsters; i++) {
            if (!iter.hasNext()) {
                return;
            }

            var targetLoc = iter.next();
            var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ZOMBIE, "Attacker");
            var spawnLoc = targetLoc.clone().add(0, 1, 0);
            npc.spawn(spawnLoc);
            npc.setProtected(false);

            var attackKingdomGoal = new TargetClaimAI(kingdom, npc, 0.01f);
            npc.getDefaultGoalController().addGoal(attackKingdomGoal.executor(), 1);

            kingdom.getAttackers().put(npc.getUniqueId(), npc);
        }
    }

    public void locateSpawns() {
        var allKingdoms = Kingdom.getKingdomIndex()
                .values().toArray(new Kingdom[0]);
        for (var kingdom : allKingdoms) {
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

        Kingdoms.scheduler.runTask(() -> doSpawns(kingdom, spawnLoc.iterator()));
    }

    private Location trySpawn(Kingdom k, int x, int z) {
        for (int y = 0; y < spawnRadius; y++) {
            var testLoc = new Location(k.getBlock().getWorld(), x, k.getBlock().getBlockY() + y, z);
            if (canSpawn(testLoc)) {
                return testLoc;
            }
        }

        for (int y = 0; y < spawnRadius; y++) {
            var testLoc = new Location(k.getBlock().getWorld(), x, k.getBlock().getBlockY() - y, z);
            if (canSpawn(testLoc)) {
                return testLoc;
            }
        }

        return null;
    }

    private boolean canSpawn(Location l) {
        return l.getBlock().getType().isSolid() && !spawnable(l.getBlock().getType()) &&
                l.clone().add(0, 1, 0).getBlock().getType() == Material.AIR &&
                l.clone().add(0, 2, 0).getBlock().getType() == Material.AIR;
    }

    private boolean spawnable(Material m) {
        switch (m) {
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case MANGROVE_LEAVES:
            case JUNGLE_LEAVES:
            case AZALEA_LEAVES:
            case CHERRY_LEAVES:
                return false;
            default:
                return true;
        }
    }

}
