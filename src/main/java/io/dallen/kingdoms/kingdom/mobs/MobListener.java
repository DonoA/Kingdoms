package io.dallen.kingdoms.kingdom.mobs;

import io.dallen.kingdoms.kingdom.Kingdom;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        for (var kingdom : Kingdom.getKingdomIndex().values()) {
            var attacker = kingdom.getAttackers().get(e.getEntity().getUniqueId());
            if (attacker != null) {
                kingdom.getAttackers().remove(e.getEntity().getUniqueId());
                Bukkit.broadcastMessage("Attacker of " + kingdom.getName() + " killed");
                return;
            }
        }
    }
}
