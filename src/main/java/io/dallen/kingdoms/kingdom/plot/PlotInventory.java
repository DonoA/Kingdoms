package io.dallen.kingdoms.kingdom.plot;

import lombok.Getter;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlotInventory implements Inventory {

    @Getter
    List<Location> chests = new ArrayList<>();

    private List<Inventory> getChestInventories() {
        var inventories = new ArrayList<Inventory>();
        var checkedChests = new HashSet<Location>();
        for (var loc : chests) {
            var chestData = loc.getBlock().getState();
            if (!(chestData instanceof Chest)) {
                continue;
            }
            var chestInv = ((Chest) chestData).getInventory();
            var chestLoc = chestInv.getLocation();
            if (checkedChests.contains(chestLoc)) {
                continue;
            }
            checkedChests.add(chestLoc);
            inventories.add(chestInv);
        }
        return inventories;
    }

    @NotNull
    @Override
    public ListIterator<ItemStack> iterator() {
        return getChestInventories().stream()
                .map(Inventory::getContents)
                .flatMap(Stream::of)
                .collect(Collectors.toList())
                .listIterator();
    }

    @NotNull
    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        var invs = getChestInventories();
        List<ItemStack> itemList = new ArrayList<>(Arrays.asList(itemStacks));
        var result = new HashMap<Integer, ItemStack>();
        for (var inv :  invs) {
            result = inv.addItem(itemList.toArray(new ItemStack[0]));
            var toRemove = new ArrayList<Integer>();
            for (var i = 0; i < itemList.size(); i++) {
                var newAmount = result.get(i);
                if (newAmount == null) {
                    toRemove.add(i);
                } else {
                    itemList.set(i, newAmount);
                }
            }

            toRemove.forEach(index -> itemList.remove(index.intValue()));
        }
        return result;
    }

    public int first(@NotNull Material material) throws IllegalArgumentException {
        return firstOf(Set.of(material));
    }

    public int firstOf(@NotNull Set<Material> materials) throws IllegalArgumentException {
        var invs = getChestInventories();
        var idx = -1;
        for (var inv : invs) {
            for (var item : inv.getContents()) {
                idx++;

                if (item == null) {
                    continue;
                }

                if (materials.contains(item.getType())) {
                    return idx;
                }
            }
        }
        return -1;
    }

    @Override
    public int getSize() {
        return getChestInventories().stream()
                .mapToInt(Inventory::getSize)
                .sum();
    }

    private Pair<Inventory, Integer> getInvForOffset(int i) {
        var invs = getChestInventories();
        for (var inv : invs) {
            if (i <= inv.getSize()) {
                return Pair.of(inv, i);
            } else {
                i -= inv.getSize();
            }
        }

        return null;
    }

    @Nullable
    @Override
    public ItemStack getItem(int i) {
        var result = getInvForOffset(i);
        if (result == null) {
            return null;
        }
        return result.getLeft().getItem(result.getRight());
    }

    @Override
    public void setItem(int i, @Nullable ItemStack itemStack) {
        var result = getInvForOffset(i);
        if (result == null) {
            return;
        }

        result.getLeft().setItem(result.getRight(), itemStack);
    }

    @Override
    public void clear(int i) {
        var result = getInvForOffset(i);
        if (result == null) {
            return;
        }

        result.getLeft().clear(result.getRight());
    }

    @Override
    public void clear() {
        getChestInventories().forEach(Inventory::clear);
    }

    @Override
    public int getMaxStackSize() {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public void setMaxStackSize(int i) {
        throw new NotImplementedException("Dummy Inventory");
    }

    @NotNull
    @Override
    public HashMap<Integer, ItemStack> removeItem(@NotNull ItemStack... itemStacks) throws IllegalArgumentException {
        throw new NotImplementedException("Dummy Inventory");
    }

    @NotNull
    @Override
    public ItemStack[] getContents() {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public void setContents(@NotNull ItemStack[] itemStacks) throws IllegalArgumentException {
        throw new NotImplementedException("Dummy Inventory");
    }

    @NotNull
    @Override
    public ItemStack[] getStorageContents() {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public void setStorageContents(@NotNull ItemStack[] itemStacks) throws IllegalArgumentException {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public boolean contains(@NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public boolean contains(@Nullable ItemStack itemStack) {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public boolean contains(@NotNull Material material, int i) throws IllegalArgumentException {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public boolean contains(@Nullable ItemStack itemStack, int i) {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public boolean containsAtLeast(@Nullable ItemStack itemStack, int i) {
        throw new NotImplementedException("Dummy Inventory");
    }

    @NotNull
    @Override
    public HashMap<Integer, ? extends ItemStack> all(@NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedException("Dummy Inventory");
    }

    @NotNull
    @Override
    public HashMap<Integer, ? extends ItemStack> all(@Nullable ItemStack itemStack) {
        throw new NotImplementedException("Dummy Inventory");
    }


    @Override
    public int first(@NotNull ItemStack itemStack) {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public int firstEmpty() {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public boolean isEmpty() {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public void remove(@NotNull Material material) throws IllegalArgumentException {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Override
    public void remove(@NotNull ItemStack itemStack) {
        throw new NotImplementedException("Dummy Inventory");
    }



    @NotNull
    @Override
    public List<HumanEntity> getViewers() {
        throw new NotImplementedException("Dummy Inventory");
    }

    @NotNull
    @Override
    public InventoryType getType() {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Nullable
    @Override
    public InventoryHolder getHolder() {
        throw new NotImplementedException("Dummy Inventory");
    }

    @NotNull
    @Override
    public ListIterator<ItemStack> iterator(int i) {
        throw new NotImplementedException("Dummy Inventory");
    }

    @Nullable
    @Override
    public Location getLocation() {
        throw new NotImplementedException("Dummy Inventory");
    }
}
