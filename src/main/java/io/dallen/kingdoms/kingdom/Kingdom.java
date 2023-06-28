package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.savedata.SaveData;
import io.dallen.kingdoms.savedata.kingdom.KingdomSaveData;
import io.dallen.kingdoms.util.Bounds;
import io.dallen.kingdoms.util.OfflinePlayerUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
public class Kingdom implements SaveData<Kingdom> {

    public final static Material outlineMaterial = Material.OBSIDIAN;
    public final static int defaultSize = 10;

    @Getter
    private final static KingdomSaveData kingdomIndex =
            new KingdomSaveData();

    @Getter
    private final String name;

    @Getter
    private final Location claim;

    @Getter
    private final OfflinePlayer owner;

    @Getter
    private  List<OfflinePlayer> members = new ArrayList<>();

    @Getter
    private AreaBounds areaBounds;

    @Getter
    private Map<UUID, NPC> attackers = new HashMap<>();

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
        for (var kingdom : kingdomIndex.values()) {
            if (kingdom.getAreaBounds().getBounds().contains(location.getBlockX(), location.getBlockZ())) {
                return kingdom;
            }
        }

        return null;
    }

    public static void register(Kingdom kingdom) {
        kingdomIndex.put(kingdom.getName(), kingdom);
    }

    public void destroy() {
        OfflinePlayerUtil.trySendMessage(owner, "Your kingdom has fallen!");
        destoryed = true;
        eraseBounds();
        attackers.values().forEach(NPC::destroy);
        kingdomIndex.remove(getName());
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

    @Override
    public Ref<Kingdom> asRef() {
        return Ref.create(kingdomIndex, name);
    }
}
