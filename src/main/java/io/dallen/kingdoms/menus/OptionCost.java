package io.dallen.kingdoms.menus;

import lombok.Builder;
import lombok.Singular;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
public class OptionCost {

    @Singular
    private Map<Material, Integer> requirements;

    public boolean canPurchase(Inventory inv) {
        var accounting = new HashMap<Material, Integer>();
        var accountedFor = new HashSet<Material>();
        for (var item : inv) {
            if (item == null || !requirements.containsKey(item.getType())) {
                continue;
            }

            var mat = item.getType();

            var count = accounting.getOrDefault(mat, 0);
            accounting.put(mat, count + item.getAmount());

            if (count >= requirements.get(mat)) {
                accountedFor.add(mat);
            }
        }

        return requirements.size() == accountedFor.size();
    }

    public void purchase(Inventory inv) {
        var accounting = new HashMap<Material, Integer>();
        for (var item : inv) {
            if (item == null || !requirements.containsKey(item.getType())) {
                continue;
            }

            var mat = item.getType();

            var count = accounting.getOrDefault(mat, 0);
            var required = requirements.get(mat);
            var remaining = required - count;
            if (remaining <= 0) {
                continue;
            }

            if (remaining > item.getAmount()) {
                count += item.getAmount();
                item.setType(Material.AIR);
            } else {
                count += remaining;
                item.setAmount(item.getAmount() - remaining);
            }
            accounting.put(mat, count);
        }
    }

    public String[] requirements() {
        return requirements.entrySet().stream()
                .map((entry) -> entry.getKey() + " x" + entry.getValue())
                .toArray(String[]::new);
    }

}
