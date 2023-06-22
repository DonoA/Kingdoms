package io.dallen.kingdoms.kingdom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@RequiredArgsConstructor
public class AttackingNPC {
    @Getter
    private final NPC base;
    @Getter @Setter
    private Location blockInWay;

    public static AttackingNPC createNPC(EntityType typ) {
        var npc = CitizensAPI.getNPCRegistry().createNPC(typ, "Attacker");
        return new AttackingNPC(npc);
    }

    public int getEntityId() {
        return base.getEntity().getEntityId();
    }
}
