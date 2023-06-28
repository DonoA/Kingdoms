package io.dallen.kingdoms.customblocks.blocks;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.customblocks.CustomBlock;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.savedata.Ref;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class ClaimBlock extends CustomBlock {

    @AllArgsConstructor
    public static class ClaimBlockData extends CustomBlockData {

        static {
            CustomBlockData.registerSubclass(ClaimBlockData.class);
        }

        public Ref<Kingdom> kingdom;
        public OfflinePlayer owner;
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
            claimData.kingdom = newKingdom.asRef();
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

        claimData.kingdom.get().destroy();
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
