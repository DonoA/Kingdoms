package io.dallen.kingdoms.customblocks.blocks;

import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.customblocks.CustomBlockData;
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

    public ClaimBlock(Material base) {
        super("Claim", base);
    }

    @Override
    public void onPlace(BlockPlaceEvent blockPlaceEvent) {
        var claimData = new ClaimBlockData(null, blockPlaceEvent.getPlayer());
        CustomBlockData.setBlockData(blockPlaceEvent.getBlock().getLocation(), claimData);

        ChestGUI gui = new ChestGUI("Claim", InventoryType.ANVIL);
        gui.setClickHandler((menuEvent) -> {
            var anvilMenu = (ChestGUI.AnvilMenuInstance) menuEvent.getMenu();
            var claimName = anvilMenu.getCurrentItemName();
            var newKingdom = new Kingdom(claimName, blockPlaceEvent.getBlock().getLocation(), blockPlaceEvent.getPlayer());
            Kingdom.register(newKingdom);
            newKingdom.placeBounds();
            claimData.kingdom = newKingdom;
            blockPlaceEvent.getPlayer().sendMessage("Created kingdom " + newKingdom.getName());
        });

        gui.setOption(0, CustomItemIndex.SUBMIT.toItemStack(), "Name Claim");
        gui.setCloseEvent((closeEvent -> {
            blockPlaceEvent.getBlockPlaced().breakNaturally();
        }));
        gui.sendMenu(blockPlaceEvent.getPlayer());
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        var claimData = CustomBlockData.removeBlockData(event.getBlock().getLocation(), ClaimBlockData.class);

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("Claim cannot be broken");
            return;
        }

        if (claimData == null || claimData.kingdom == null) {
            return;
        }

        claimData.kingdom.destroy();
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        var claimData = CustomBlockData.getBlockData(event.getClickedBlock().getLocation(), ClaimBlockData.class);
        if (claimData == null) {
            return;
        }
        event.getPlayer().sendMessage("This claim is owned by " + claimData.owner);
    }
}
