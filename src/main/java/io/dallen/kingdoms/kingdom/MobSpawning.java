package io.dallen.kingdoms.kingdom;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.goals.TargetNearbyEntityGoal;
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
import java.util.Set;
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
            var npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.ZOMBIE, "Attacker");
            var spawnLoc = targetLoc.clone().add(0, 1, 0);
            npc.spawn(spawnLoc);
            npc.setProtected(false);

            var attackKingdomGoal = TargetKingdomClaimGoal.builder()
                    .npc(npc)
                    .targetKingdom(kingdom)
                    .build();
            npc.getDefaultGoalController().addGoal(attackKingdomGoal, 100);

            var attackPlayerGoal = TargetEntityGoal.builder()
                    .npc(npc)
                    .aggressive(true)
                    .radius(spawnRadius * 2)
                    .targets(Set.of(EntityType.PLAYER)).build();
            npc.getDefaultGoalController().addGoal(attackPlayerGoal, 99);

//            var navigator = npc.getNavigator();
//            navigator.getLocalParameters()
//                    .attackRange(5D)
//                    .attackDelayTicks(2)
//                    .updatePathRate(1)
//                    .baseSpeed(1F);

            kingdom.getAttackers().put(npc.getEntity().getEntityId(), npc);
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
