package io.dallen.kingdoms.customitems;

import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.util.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

public class DebugTool extends CustomItem {

    public DebugTool(Material baseItem) {
        super("Debug Tool", baseItem, true);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        var block = event.getClickedBlock();
        var player = event.getPlayer();
        if (block == null) {
            return;
        }
        event.setCancelled(true);

        var data = CustomBlockData.getBlockData(block.getLocation(), CustomBlockData.class);
        if (data == null) {
            player.sendMessage("No block data");
            return;
        }

        player.sendMessage(data.toString());
    }
}
