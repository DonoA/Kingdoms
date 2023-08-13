package io.dallen.kingdoms.kingdom.plot.controller;

import com.google.gson.annotations.Expose;
import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.blocks.PlotChest;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.menus.OptionCost;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.util.ItemStackMaterial;
import io.dallen.kingdoms.util.ItemUtil;
import io.dallen.kingdoms.util.Lazy;
import io.dallen.kingdoms.util.MaterialUtil;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor
public class Storage extends PlotController {
    private final static Material floorMaterial = Material.OAK_PLANKS;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

    private final PlotInventory storageInventory = new PlotInventory();

    @Expose(serialize = false, deserialize = false)
    private final Lazy<BasicPlotGUI> controllerMenu = new Lazy<>(() ->
            new BasicPlotGUI("Storage", plot));

    private final PlotRequirement inputChest = new PlotRequirement("Input Chest");
    private final PlotRequirement outputChest = new PlotRequirement("Output Chest");
    private final PlotRequirement storageSpace = new PlotRequirement("Storage Space");

    public List<PlotRequirement> getAllReqs() {
        return List.of(
                inputChest, outputChest, storageSpace
        );
    }

    @Override
    public List<PlotInventory> getAllPlotInventories() {
        return List.of(
                storageInventory
        );
    }

    public Storage(Ref<Plot> plot) {
        super(plot, "Storage");
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
    }

    @Override
    public void onDestroy() {
        getPlot().setFloor(Material.DIRT);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        scanPlotAsync();
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        scanPlotAsync();
    }

    @Override
    protected void scanBlock(Location blocLoc, Material typ) {
        if (!Material.CHEST.equals(typ)) {
            return;
        }

        var chestData = CustomBlockData.getBlockData(blocLoc, PlotChest.ChestMetadata.class);
        PlotChest.PlotChestType chestType = null;
        if (chestData != null) {
            chestType = chestData.getTyp();
        }

        if (chestType == PlotChest.PlotChestType.INPUT) {
            inputChest.setPoi(blocLoc);
        } else if (chestType == PlotChest.PlotChestType.OUTPUT) {
            outputChest.setPoi(blocLoc);
        } else {
            storageInventory.getChests().add(blocLoc);
            storageSpace.setPoi(blocLoc);
        }
    }

    @Override
    public ChestGUI getPlotMenu() {
        scanPlot();
        controllerMenu.get().updateWithReqs(getAllReqs());

        controllerMenu.get().refreshAllViewers();
        return controllerMenu.get();
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        var blocLoc = event.getClickedBlock().getLocation();
        var chestData = CustomBlockData.getBlockData(blocLoc, PlotChest.ChestMetadata.class);

        if (chestData == null || chestData.getTyp() != PlotChest.PlotChestType.OUTPUT) {
            return;
        }

        event.setCancelled(true);

        var gui = getStorageGUI();
        gui.sendMenu(event.getPlayer());
    }

    @NotNull
    private ChestGUI getStorageGUI() {
        var allItems = new HashMap<ItemStackMaterial, Integer>();
        for (var item : storageInventory.getContents()) {
            if (item == null) {
                continue;
            }

            var itemStackKey = new ItemStackMaterial(item);
            var count = allItems.getOrDefault(itemStackKey, 0);
            allItems.put(itemStackKey, count + item.getAmount());
        }

        var menuSize = allItems.size();
        if (menuSize < 27) {
            menuSize = 27;
        }

        var gui = new ChestGUI("Storage", menuSize);
        var idx = 0;
        for (var entry : allItems.entrySet()) {
            var icon = entry.getKey().toItemStack();
            ItemUtil.setItemLore(icon, "Count " + entry.getValue());
            gui.setOption(idx, icon);
            idx++;
        }
        gui.setClickHandler(handleStorageClick(allItems));
        return gui;
    }

    private Consumer<ChestGUI.OptionClickEvent> handleStorageClick(Map<ItemStackMaterial, Integer> allItems) {
        return (event) -> {
            var clicked = event.getClicked();
            if (clicked.getType() == Material.AIR) {
                event.setClose(false);
                return;
            }

            var pickupItem = new ItemStack(clicked.getType());
            ItemUtil.setItemNameAndLore(pickupItem, clicked.getItemMeta().getDisplayName());
            var possibleAmount = allItems.get(new ItemStackMaterial(clicked)); // Does not select the item count correctly
            var actualAmount = Math.min(possibleAmount, pickupItem.getMaxStackSize());
            pickupItem.setAmount(actualAmount);

            event.getMenu().getView().setCursor(pickupItem);
            storageInventory.remove(pickupItem);

            event.setNext(getStorageGUI());
        };
    }

    @Override
    public void tick() {
        if (inputChest.getPoi() == null) {
            return;
        }

        var inchestBlock = inputChest.getPoi().getBlock();
        var inputInventory = (Chest) inchestBlock.getState();
        var inv = inputInventory.getBlockInventory();
        if (inv.isEmpty()) {
            return;
        }

        for (int i = 0; i < inv.getSize(); i++) {
            var item = inv.getItem(i);
            if (item == null) {
                continue;
            }

            storageInventory.addItem(item);
            inv.clear(i);
        }
    }
}
