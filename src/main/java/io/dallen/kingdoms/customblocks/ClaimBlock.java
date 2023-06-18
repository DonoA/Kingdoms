package io.dallen.kingdoms.customblocks;

import io.dallen.kingdoms.menus.ChestGUI;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ClaimBlock extends CustomBlock {

    ChestGUI gui;

    public ClaimBlock(Material base) {
        super("Claim", base);

        gui = new ChestGUI("Claim", InventoryType.CHEST, (event) -> {
            event.getPlayer().sendMessage("Clicked " + event.getName());
        });

        gui.setOption(0, new ItemStack(Material.IRON_INGOT), "Default");
    }

    void onPlace(BlockPlaceEvent event) {
        event.getPlayer().sendMessage("Placed Claim");
    }

    void onBreak(BlockBreakEvent event) {
        event.getPlayer().sendMessage("Claim broken");
    }

    void onInteract(PlayerInteractEvent event) {
        gui.sendMenu(event.getPlayer());
    }
}
