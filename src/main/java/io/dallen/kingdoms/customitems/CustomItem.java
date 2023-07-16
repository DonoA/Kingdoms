package io.dallen.kingdoms.customitems;

import io.dallen.kingdoms.util.ItemUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CustomItem {

    public static Map<Material, CustomItem> usedMaterials = new HashMap<>();

    private final String name;
    private final Material baseItem;

    @Getter
    private final boolean holdable;

    public CustomItem(String name, Material baseItem, boolean holdable) {
        if (baseItem.isBlock()) {
            throw new UnsupportedOperationException(baseItem + " must only be an item type");
        }
        this.holdable = holdable;
        this.name = name;
        this.baseItem = baseItem;

        var currentAssignment = usedMaterials.get(baseItem);
        if (currentAssignment != null) {
            throw new UnsupportedOperationException(baseItem + " already used for " + currentAssignment.name);
        }

        usedMaterials.put(baseItem, this);
    }

    public CustomItem(String name, Material baseItem) {
        this(name, baseItem, false);
    }


    public Material toMaterial() {
        return baseItem;
    }

    public ItemStack toItemStack() {
        return ItemUtil.setItemNameAndLore(baseItem, name);
    }

    public void onInteract(PlayerInteractEvent event) { }
}
