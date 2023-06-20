package io.dallen.kingdoms.customblocks;

import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.menus.ChestGUI;
import lombok.AllArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClaimBlock extends CustomBlock {

    @AllArgsConstructor
    private static class ClaimBlockData extends CustomBlockData {
        public Kingdom kingdom;
        public Player owner;
    }

    ChestGUI gui;

    public ClaimBlock(Material base) {
        super("Claim", base);
    }

    void onPlace(BlockPlaceEvent event) {
        CustomBlockData.setBlockData(event.getBlock().getLocation(), new ClaimBlockData(null, event.getPlayer()));

        ChestGUI gui = new ChestGUI("Claim", InventoryType.ANVIL, (menuEvent) -> {
            var anvilMenu = (ChestGUI.AnvilMenuInstance) menuEvent.getMenu();
            var claimName = anvilMenu.getCurrentItemName();
            var newKingdom = new Kingdom(claimName, event.getBlock().getLocation(), event.getPlayer());
            Kingdom.register(newKingdom);
            event.getPlayer().sendMessage("Created kingdom " + newKingdom.getName());
        });

        gui.setOption(0, CustomItemIndex.Submit.itemStack(), "Name Claim");
        gui.sendMenu(event.getPlayer());
    }

    void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
        event.getPlayer().sendMessage("Claim cannot be broken");
    }

    void onInteract(PlayerInteractEvent event) {
        var claimData = CustomBlockData.getBlockData(event.getClickedBlock().getLocation(), ClaimBlockData.class);
        if (claimData == null) {
            return;
        }
        event.getPlayer().sendMessage("This claim is owned by " + claimData.owner);
    }
}
