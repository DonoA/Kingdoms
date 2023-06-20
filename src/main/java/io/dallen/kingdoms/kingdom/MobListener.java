package io.dallen.kingdoms.kingdom;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        for (var kingdom : Kingdom.getAllKingdoms().values()) {
            var attacker = kingdom.getAttackers().get(e.getEntity().getEntityId());
            if (attacker != null) {
                kingdom.getAttackers().remove(e.getEntity().getEntityId());
                Bukkit.broadcastMessage("Attacker of " + kingdom.getName() + " killed");
                return;
            }
        }
    }
}
