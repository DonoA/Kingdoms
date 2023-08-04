package io.dallen.kingdoms.customblocks;

import io.dallen.kingdoms.customitems.CustomItem;
import io.dallen.kingdoms.util.ItemUtil;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CustomBlockListener implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        var placedType = event.getBlockPlaced().getType();
        var customPlaced = CustomBlock.usedMaterials.get(placedType);
        if (customPlaced == null) {
            return;
        }

        customPlaced.onPlace(event);
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        var placedType = event.getBlock().getType();
        var customPlaced = CustomBlock.usedMaterials.get(placedType);
        if (customPlaced == null) {
            return;
        }

        customPlaced.onBreak(event);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }

        var placedType = event.getClickedBlock().getType();
        var customPlaced = CustomBlock.usedMaterials.get(placedType);
        if (customPlaced == null) {
            return;
        }

        customPlaced.onInteract(event);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        checkItem(event.getItem().getItemStack());
        if (event.getEntity() instanceof Player) {
            renameBlocks(((Player) event.getEntity()).getInventory());
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        renameBlocks(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        renameBlocks(event.getPlayer().getInventory());
    }

    private void renameBlocks(Inventory inv) {
        for (var item : inv.getContents()) {
            if (item == null) {
                continue;
            }

            checkItem(item);
        }
    }

    private void checkItem(ItemStack item) {
        var customBlock = CustomBlock.usedMaterials.get(item.getType());
        if (customBlock == null) {
            return;
        }

        ItemUtil.ensureItemName(item, customBlock.getName());
    }

}
