package io.dallen.kingdoms.kingdom;

import io.dallen.kingdoms.customitems.CustomItem;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import lombok.Builder;
import lombok.Singular;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CraftingListener implements Listener {

    public static Set<Material> craftingBlocklist = new HashSet<>();

    @Builder
    public static class Recipe {
        private final String name;
        private final Material toRemove;
        private final ItemStack result;
        private final String topRow;
        private final String midRow;
        private final String btmRow;
        @Singular
        private final Map<Character, Material> itemMaps;

        public void register(Plugin plugin) {
            if (toRemove != null) {
                craftingBlocklist.add(toRemove);
            }

            var namespace = new NamespacedKey(plugin, name);
            var newResult = new ShapedRecipe(namespace, result);

            if (btmRow == null) {
                if (midRow == null) {
                    newResult.shape(topRow);
                } else {
                    newResult.shape(topRow, midRow);
                }
            } else {
                newResult.shape(topRow, midRow, btmRow);
            }

            for (var entry : itemMaps.entrySet()) {
                newResult.setIngredient(entry.getKey(), entry.getValue());
            }

            Bukkit.addRecipe(newResult);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        var recipe = event.getRecipe();
        var result = recipe.getResult().getType();
        if (craftingBlocklist.contains(result)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.WORKBENCH) {
            return;
        }

        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        var resultStack = ((CraftItemEvent) event).getRecipe().getResult();
        var customItem = CustomItem.usedMaterials.get(resultStack.getType());
        if (customItem == null || customItem.isHoldable()) {
            return;
        }

        var playerInv = event.getWhoClicked().getInventory();
        Arrays.stream(event.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> !CustomItem.usedMaterials.containsKey(item.getType()))
                .forEach(playerInv::addItem);

        event.getInventory().clear();

        event.getWhoClicked().sendMessage("Cannot be crafted by hand!");
        event.setCancelled(true);
    }


}
