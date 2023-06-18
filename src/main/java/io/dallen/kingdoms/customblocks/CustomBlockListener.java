package io.dallen.kingdoms.customblocks;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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

        var placedType = event.getClickedBlock().getType();
        var customPlaced = CustomBlock.usedMaterials.get(placedType);
        if (customPlaced == null) {
            return;
        }

        customPlaced.onInteract(event);
    }

}
