package io.dallen.kingdoms.customitems;

import io.dallen.kingdoms.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CustomItemListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        var mat = event.getMaterial();
        var customItem = CustomItem.usedMaterials.get(mat);
        if (customItem == null) {
            return;
        }

        customItem.onInteract(event);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        removeContraband(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        removeContraband(event.getPlayer());
    }

    private void removeContraband(HumanEntity player) {
        var playerInv = player.getInventory();
        for (var item : playerInv.getContents()) {
            if (item == null) {
                continue;
            }

            var customItem = CustomItem.usedMaterials.get(item.getType());
            if (customItem == null) {
                continue;
            }

            if (customItem.isHoldable()) {
                ItemUtil.ensureItemName(item, customItem.getName());
            } else {
                playerInv.remove(customItem.toMaterial());
                Bukkit.getLogger().info("Removed " + customItem.toMaterial().name() + " from " + player.getName());
            }
        }
    }



}
