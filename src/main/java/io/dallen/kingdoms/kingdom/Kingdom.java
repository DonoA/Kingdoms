package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.util.Bounds;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private AreaBounds areaBounds;

    @Getter
    private Map<Integer, NPC> attackers = new HashMap<>();

    @Getter
    private boolean destoryed = false;

    public Kingdom(String name, Location claim, Player owner) {
        this.name = name;
        this.claim = claim;
        this.owner = owner;

        var bounds = Bounds.builder()
                .blockX(claim.getBlockX())
                .blockY(claim.getBlockY())
                .blockZ(claim.getBlockZ())
                .height(10)
                .plusX(defaultSize)
                .minusX(defaultSize)
                .plusZ(defaultSize)
                .minusZ(defaultSize)
                .build();

        areaBounds = new AreaBounds(bounds, true);
    }

    public static Kingdom getOwner(Location location) {
        for (var kingdom : allKingdoms.values()) {
            if (kingdom.getAreaBounds().getBounds().contains(location.getBlockX(), location.getBlockZ())) {
                return kingdom;
            }
        }

        return null;
    }

    public static void register(Kingdom kingdom) {
        allKingdoms.put(kingdom.getName(), kingdom);
    }

    public void destroy() {
        owner.sendMessage("Your kingdom has fallen!");
        destoryed = true;
        eraseBounds();
        attackers.values().forEach(NPC::destroy);
        allKingdoms.remove(getName());
    }

    public void placeBounds() {
        areaBounds.placeBounds(claim.getWorld(), outlineMaterial);
    }

    public void eraseBounds() {
        areaBounds.eraseBounds(claim.getWorld());
    }

    public Bounds getBounds() {
        return areaBounds.getBounds();
    }
}
