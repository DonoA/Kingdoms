package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.savedata.FileManager;
import io.dallen.kingdoms.savedata.SaveDataManager;
import io.dallen.kingdoms.util.Bounds;
import io.dallen.kingdoms.util.OfflinePlayerUtil;
import lombok.Getter;
import lombok.ToString;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@ToString
public class Kingdom extends ClaimedRegion<String, Kingdom> {

    public final static Material outlineMaterial = Material.OBSIDIAN;
    public final static int defaultSize = 20;

    @Getter
    private final static SaveDataManager<String, Kingdom> kingdomIndex =
            new SaveDataManager<>(FileManager.KINGDOMS_SAVE_DATA, String.class);

    @Getter
    private final String name;

    @Getter
    private final OfflinePlayer owner;

    @Getter
    private Set<OfflinePlayer> members = new HashSet<>();

    @Getter
    private Map<UUID, NPC> attackers = new HashMap<>();

    @Getter
    private Map<String, String> blueprints = new HashMap<>();

    @Getter
    private boolean destoryed = false;

    public Kingdom(String name, Location claim, Player owner) {
        super(defaultBounds(claim), claim);
        this.name = name;
        this.owner = owner;
        members.add(owner);
    }

    private static AreaBounds defaultBounds(Location claim) {
        var bounds = Bounds.builder()
                .world(claim.getWorld())
                .blockX(claim.getBlockX())
                .blockY(claim.getBlockY())
                .blockZ(claim.getBlockZ())
                .height(100)
                .depth(50)
                .plusX(defaultSize)
                .minusX(defaultSize)
                .plusZ(defaultSize)
                .minusZ(defaultSize)
                .build();

        return new AreaBounds(bounds, true);
    }

    public static Kingdom findKingdom(Location location) {
        for (var kingdom : kingdomIndex.values()) {
            if (kingdom.getBounds().contains(location.getBlockX(), location.getBlockY(), location.getBlockZ())) {
                return kingdom;
            }
        }

        return null;
    }

    public void destroy() {
        OfflinePlayerUtil.trySendMessage(owner, "Your kingdom has fallen!");
        destoryed = true;
        attackers.values().forEach(NPC::destroy);
        super.destroy();
    }

    @Override
    protected Material getBorderMaterial() {
        return outlineMaterial;
    }

    @Override
    protected SaveDataManager<String, Kingdom> getIndex() {
        return kingdomIndex;
    }

    @Override
    protected String getId() {
        return name;
    }

    public boolean isMember(OfflinePlayer player) {
        return members.contains(player);
    }

    public boolean isOnline() {
        for (var offlinePlayer : members) {
            if (offlinePlayer.isOnline()) {
                return true;
            }
        }

        return owner.isOnline();
    }

    public String getBlueprintForName(String name) {
        return blueprints.get(name);
    }

    public Set<String> blueprintNames() {
        return blueprints.keySet();
    }
}
