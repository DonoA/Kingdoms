package io.dallen.kingdoms.util;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Data
public class ItemStackMaterial {
    private final Material mat;

    @Nullable
    private final String name;

    public ItemStackMaterial(ItemStack is) {
        this.mat = is.getType();
        String name = "";
        if (is.hasItemMeta()) {
            name = is.getItemMeta().getDisplayName();
        }

        this.name = name;
    }

    public ItemStack toItemStack() {
        var is = new ItemStack(this.mat);

        if (!"".equals(name)) {
            var meta = is.getItemMeta();
            meta.setDisplayName(name);
            is.setItemMeta(meta);
        }

        return is;
    }

}
