package io.dallen.kingdoms.customitems;

import io.dallen.kingdoms.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomItem {

    public static Map<Material, CustomItem> usedMaterials = new HashMap<>();

    private final String name;
    private final Material baseItem;

    public CustomItem(String name, Material baseItem) {
        if (baseItem.isBlock()) {
            throw new UnsupportedOperationException(baseItem + " must only be an item type");
        }
        this.name = name;
        this.baseItem = baseItem;

        var currentAssignment = usedMaterials.get(baseItem);
        if (currentAssignment != null) {
            throw new UnsupportedOperationException(baseItem + " already used for " + currentAssignment.name);
        }

        usedMaterials.put(baseItem, this);
    }


    public Material toMaterial() {
        return baseItem;
    }

    public ItemStack itemStack() {
        return ItemUtil.setItemNameAndLore(baseItem, name);
    }

    void onInteract(PlayerInteractEvent event) { }
}
