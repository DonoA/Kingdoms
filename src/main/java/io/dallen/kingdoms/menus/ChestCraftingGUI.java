package io.dallen.kingdoms.menus;

import com.google.gson.annotations.Expose;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.savedata.SubClass;
import io.dallen.kingdoms.util.ItemUtil;
import io.dallen.kingdoms.util.Lazy;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class ChestCraftingGUI {

    @Data
    @Builder
    public static class CraftingRecipe {
        private final Material product;
        private final OptionCost cost;
        private final long timeToCraft;
    }

    @Getter
    private String title;
    private List<CraftingRecipe> items;
    private Map<Material, Integer> itemOrder;
    private PlotInventory plotInputs;

    private final int[] currentCrafting = new int[9];
    @Setter
    private boolean enabled = false;

    @Expose(serialize = false, deserialize = false)
    private final Lazy<ChestGUI> menu = new Lazy<>(() -> {
        var gui = new ChestGUI(getTitle(), 9*5);
        gui.setClickHandler(this::handleClick);
        return gui;
    });

    @Builder
    public ChestCraftingGUI(String title, @Singular List<CraftingRecipe> items, PlotInventory plotInputs) {
        this.title = title;
        this.items = items;
        this.itemOrder = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            itemOrder.put(items.get(i).getProduct(), i);
        }
        this.plotInputs = plotInputs;
    }

    public ItemStack[] getItems() {
        var menuItems = new ItemStack[9 * 5];

        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var currentlyCrafting = currentCrafting[i];
            var lore = new ArrayList<String>();
            lore.add("Crafting " + currentlyCrafting);
            lore.add("Requires:");
            lore.addAll(Arrays.asList(item.getCost().requirements()));
            lore.add("Takes " + item.getTimeToCraft() + " seconds");
            var icon = ItemUtil.setItemNameAndLore(
                    item.getProduct(),
                    "Craft " + item.getProduct().name(),
                    lore);
            if (enabled) {
                var increase = ItemUtil.setItemNameAndLore(
                        CustomItemIndex.INCREASE.toMaterial(),
                        "Craft " + item.getProduct().name() + " ++",
                        lore);
                var decrease = ItemUtil.setItemNameAndLore(
                        CustomItemIndex.DECREASE.toMaterial(),
                        "Craft " + item.getProduct().name() + " --",
                        lore);
                menuItems[i] = increase;
                menuItems[18 + i] = decrease;

                if (item.getCost().canPurchase(plotInputs)) {
                    var canCraft = ItemUtil.setItemNameAndLore(
                            CustomItemIndex.SUBMIT.toMaterial(),
                            "Materials Available");
                    menuItems[27 + i] = canCraft;
                } else {
                    var canCraft = ItemUtil.setItemNameAndLore(
                            CustomItemIndex.CANCEL.toMaterial(),
                            "Missing required materials");
                    menuItems[27 + i] = canCraft;
                }

            } else {
                menuItems[i] = CustomItemIndex.CANCEL.toItemStack();
                menuItems[18 + i] = CustomItemIndex.CANCEL.toItemStack();
            }
            menuItems[9 + i] = icon;
        }

        return menuItems;
    }

    public ChestGUI getMenuAndRefresh() {
        refresh();
        return menu.get();
    }

    public void refresh() {
        var enabledText = enabled ? "Enabled" : "Disabled";
        menu.get().setName(title + " (" + enabledText + ")");
        menu.get().setOptions(getItems());
        menu.get().refreshAllViewers();
    }

    public void handleClick(ChestGUI.OptionClickEvent event) {
        var clicked = event.getSlot();
        var clickedItem = clicked % 9;
        if (clickedItem > items.size()) {
            return;
        }

        if (clicked <= 8) {
            currentCrafting[clickedItem] += 1;
        } else if (clicked >= 18) {
            currentCrafting[clickedItem] -= 1;

            if (currentCrafting[clickedItem] < 0) {
                currentCrafting[clickedItem] = 0;
            }
        }

        event.setNext(getMenuAndRefresh());
    }

    public CraftingRecipe getNextToBuild() {
        for (int i = 0; i < items.size(); i++) {
            if (currentCrafting[i] == 0) {
                continue;
            }

            var item = items.get(i);
            if (item.getCost().canPurchase(plotInputs)) {
                return items.get(i);
            }
        }

        return null;
    }

    public void removeNextToBuild(Material crafted) {
        var offset = itemOrder.get(crafted);
        currentCrafting[offset] = Math.max(currentCrafting[offset] - 1, 0);
    }
}
