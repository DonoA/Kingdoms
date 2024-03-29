package io.dallen.kingdoms.customblocks;

import io.dallen.kingdoms.util.ItemUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;


public class CustomBlock {

    public static Map<Material, CustomBlock> usedMaterials = new HashMap<>();

    @Getter
    private final String name;
    private final Material baseBlock;

    public CustomBlock(String name, Material baseBlock) {
        if (!baseBlock.isBlock()) {
            throw new UnsupportedOperationException(baseBlock + " must be a block type");
        }
        this.name = name;
        this.baseBlock = baseBlock;

        var currentAssignment = usedMaterials.get(baseBlock);
        if (currentAssignment != null) {
            throw new UnsupportedOperationException(baseBlock + " already used for " + currentAssignment.name);
        }

        usedMaterials.put(baseBlock, this);
    }


    public Material toMaterial() {
        return baseBlock;
    }

    public ItemStack itemStack() {
        return ItemUtil.setItemNameAndLore(baseBlock, name);
    }

    public void onPlace(BlockPlaceEvent event) { }

    public void onBreak(BlockBreakEvent event) { }

    public void onInteract(PlayerInteractEvent event) { }

}
