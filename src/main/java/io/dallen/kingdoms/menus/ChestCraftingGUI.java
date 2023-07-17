package io.dallen.kingdoms.menus;

import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.util.ItemUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Singular;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ChestCraftingGUI {

    @Data
    @Builder
    public static class CraftingRecipe {
        private final Material product;
        @Singular
        private final List<Pair<Material, Integer>> ingredients;
        private final long timeToCraft;
    }

    private final String title;
    private final List<CraftingRecipe> items;

    private final int[] currentCrafting = new int[9];
    @Setter
    private boolean enabled = false;

    @Builder
    public ChestCraftingGUI(String title, @Singular List<CraftingRecipe> items) {
        this.title = title;
        this.items = items;
    }

    public ChestGUI getMenu() {
        var enabledText = enabled ? "Enabled" : "Disabled";
        var gui = new ChestGUI(title + " (" + enabledText + ")", 27);
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var currentlyCrafting = currentCrafting[i];
            var lore = new ArrayList<String>();
            lore.add("Crafting " + currentlyCrafting);
            lore.add("Requires:");
            for (var req : item.getIngredients()) {
                var name = req.getKey().name();
                var count = req.getValue();
                lore.add(" - " + name + " x" + count);
            }
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
}
