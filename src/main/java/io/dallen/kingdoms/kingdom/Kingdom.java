package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.util.Bounds;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kingdom {

    public final static Material outlineMaterial = Material.OBSIDIAN;
    public final static int defaultSize = 10;

    @Getter
    private static Map<String, Kingdom> allKingdoms = new HashMap<>();

    @Getter
    private final String name;

    @Getter
    private final Location claim;

    @Getter
    private final Player owner;

    private  List<Player> members = new ArrayList<>();

    @Getter
    private Bounds bounds;
    private List<Material> previousBounds = new ArrayList<>();

    @Getter
    private Map<Integer, NPC> attackers = new HashMap<>();

    public Kingdom(String name, Location claim, Player owner) {
        this.name = name;
        this.claim = claim;
        this.owner = owner;

        bounds = Bounds.builder()
                .blockX(claim.getBlockX())
                .blockY(claim.getBlockY())
                .blockZ(claim.getBlockZ())
                .height(10)
                .plusX(defaultSize)
                .minusX(defaultSize)
                .plusZ(defaultSize)
                .minusZ(defaultSize)
                .build();
    }

    public static void register(Kingdom kingdom) {
        allKingdoms.put(kingdom.getName(), kingdom);
    }

    public static void unregister(Kingdom kingdom) {
        kingdom.eraseBounds();
        kingdom.attackers.values().forEach(NPC::destroy);
        allKingdoms.remove(kingdom.getName());
    }

    public void placeBounds() {
        previousBounds.clear();

        bounds.forEachBorder((x, z) -> {
            var block = new Location(claim.getWorld(), x, claim.getBlockY() - 1, z)
                    .getBlock();

            previousBounds.add(block.getType());
            block.setType(outlineMaterial);
        });
    }

    public void eraseBounds() {
        bounds.forEachBorder((x, z, i) -> {
            if (i >= previousBounds.size()) {
                return;
            }
            new Location(claim.getWorld(), x, claim.getBlockY() - 1, z)
                    .getBlock()
                    .setType(previousBounds.get(i));
        });
    }
}
