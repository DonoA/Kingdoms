package io.dallen.kingdoms.menus;

import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.savedata.SubClass;
import io.dallen.kingdoms.util.ItemUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Singular;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChestCraftingGUI {

    @Data
    @Builder
    public static class CraftingRecipe {
        private final Material product;
        private final OptionCost cost;
        private final long timeToCraft;
    }

    private final String title;
    private final List<CraftingRecipe> items;
    private final Map<Material, Integer> itemOrder;
    private final SubClass<Inventory> plotInputs;

    private final int[] currentCrafting = new int[9];
    @Setter
    private boolean enabled = false;

    @Builder
    public ChestCraftingGUI(String title, @Singular List<CraftingRecipe> items, Inventory plotInputs) {
        this.title = title;
        this.items = items;
        this.itemOrder = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            itemOrder.put(items.get(i).getProduct(), i);
        }
        this.plotInputs = new SubClass<>(plotInputs);
    }

    public ChestGUI getMenu() {
        var enabledText = enabled ? "Enabled" : "Disabled";
        var gui = new ChestGUI(title + " (" + enabledText + ")", 9*4);
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
                gui.setOption(i, increase);
                gui.setOption(18 + i, decrease);

                if (item.getCost().canPurchase(plotInputs.getValue())) {
                    var canCraft = ItemUtil.setItemNameAndLore(
                            CustomItemIndex.SUBMIT.toMaterial(),
                            "Materials Available");
                    gui.setOption(27 + i, canCraft);
                } else {
                    var canCraft = ItemUtil.setItemNameAndLore(
                            CustomItemIndex.CANCEL.toMaterial(),
                            "Missing required materials");
                    gui.setOption(27 + i, canCraft);
                }

            } else {
                gui.setOption(i, CustomItemIndex.CANCEL.toItemStack());
                gui.setOption(18 + i, CustomItemIndex.CANCEL.toItemStack());
            }
            gui.setOption(9 + i, icon);
        }

        gui.setClickHandler(this::handleClick);
        return gui;
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

        event.setNext(getMenu());
    }

    public CraftingRecipe getNextToBuild() {
        for (int i = 0; i < items.size(); i++) {
            if (currentCrafting[i] == 0) {
                continue;
            }

            var item = items.get(i);
            if (item.getCost().canPurchase(plotInputs.getValue())) {
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
