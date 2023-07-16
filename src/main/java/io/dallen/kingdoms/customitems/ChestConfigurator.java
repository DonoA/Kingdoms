package io.dallen.kingdoms.customitems;

import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.blocks.PlotChest;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.util.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;

public class ChestConfigurator extends CustomItem {
    public ChestConfigurator(Material baseItem) {
        super("Chest Configurator", baseItem, true);
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        var block = event.getClickedBlock();
        if (block == null || !MaterialUtil.isChest(block.getType())) {
            return;
        }
        event.setCancelled(true);

        var metadata = setupMetadata(block);
        var title = "Set Chest Type";
        if (metadata.getTyp() == PlotChest.PlotChestType.INPUT) {
            title += " (Input)";
        } else if (metadata.getTyp() == PlotChest.PlotChestType.OUTPUT) {
            title += " (Output)";
        }
        var gui = new ChestGUI(title, 9);
        gui.setOption(3, CustomItemIndex.DECREASE.toItemStack(), "Input");
        gui.setOption(5, CustomItemIndex.INCREASE.toItemStack(), "Output");

        gui.setClickHandler((click) -> {
            switch (click.getSlot()) {
                case 3:
                    metadata.setTyp(PlotChest.PlotChestType.INPUT);
                    break;
                case 5:
                    metadata.setTyp(PlotChest.PlotChestType.OUTPUT);
                    break;
                default:
                    click.setNext(gui);
                    break;
            }
        });

        gui.sendMenu(event.getPlayer());
    }

    private static PlotChest.ChestMetadata setupMetadata(Block block) {
        var metadata = CustomBlockData.getBlockData(block.getLocation(), PlotChest.ChestMetadata.class);
        if (metadata == null) {
            metadata = new PlotChest.ChestMetadata();
        }

        var chestInv = ((Chest) block.getState()).getInventory();
        if (chestInv instanceof DoubleChestInventory) {
            var leftHolder = ((DoubleChestInventory) chestInv).getLeftSide().getHolder();
            var rightHolder = ((DoubleChestInventory) chestInv).getRightSide().getHolder();

            var leftLoc = ((Chest) leftHolder).getLocation();
            var rightLoc = ((Chest) rightHolder).getLocation();
            metadata.setLeftSide(leftLoc);
            metadata.setRightSide(rightLoc);

            CustomBlockData.setBlockData(leftLoc, metadata);
            CustomBlockData.setBlockData(rightLoc, metadata);
        } else {
            metadata.setLeftSide(block.getLocation());
            CustomBlockData.setBlockData(block.getLocation(), metadata);
        }

        return metadata;
    }
}
