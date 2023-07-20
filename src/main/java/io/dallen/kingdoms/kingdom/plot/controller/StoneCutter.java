package io.dallen.kingdoms.kingdom.plot.controller;

import com.google.gson.annotations.Expose;
import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.blocks.PlotChest;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.kingdom.plot.PlotInventory;
import io.dallen.kingdoms.menus.ChestCraftingGUI;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.menus.OptionCost;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.util.Lazy;
import io.dallen.kingdoms.util.MaterialUtil;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor
public class StoneCutter extends CraftingPlotController {

    private final static Material floorMaterial = Material.COBBLESTONE;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

    private final PlotInventory inputInventory = new PlotInventory();
    private final PlotInventory outputInventory = new PlotInventory();

    private final ChestCraftingGUI crafting = ChestCraftingGUI.builder()
            .title("Stone Cutter")
            .item(ChestCraftingGUI.CraftingRecipe.builder()
                    .product(Material.STONE_PICKAXE)
                    .timeToCraft(30)
                    .cost(OptionCost.builder()
                            .requirement(Material.COBBLESTONE, 3)
                            .requirement(Material.STICK, 2)
                            .build())
                    .build())
            .item(ChestCraftingGUI.CraftingRecipe.builder()
                    .product(Material.STONE_SHOVEL)
                    .timeToCraft(30)
                    .cost(OptionCost.builder()
                            .requirement(Material.COBBLESTONE, 1)
                            .requirement(Material.STICK, 2)
                            .build())
                    .build())
            .item(ChestCraftingGUI.CraftingRecipe.builder()
                    .product(Material.STONE_HOE)
                    .timeToCraft(30)
                    .cost(OptionCost.builder()
                            .requirement(Material.COBBLESTONE, 2)
                            .requirement(Material.STICK, 2)
                            .build())
                    .build())
            .plotInputs(inputInventory)
            .build();

    @Expose(serialize = false, deserialize = false)
    private final Lazy<ChestGUI> controllerMenu = new Lazy<>(() ->
        new ChestGUI("Stone Cutter", 18));

    private NPC worker;
//    private BasicWorkerAI goal;

    private final PlotRequirement bed = new PlotRequirement("Bed");
    private final PlotRequirement inputChest = new PlotRequirement("Input Chest");
    private final PlotRequirement outputChest = new PlotRequirement("Output Chest");
    private final PlotRequirement stoneCutter = new PlotRequirement("Stone Cutting Table");

    private List<PlotRequirement> getAllReqs() {
        return List.of(
            bed, inputChest, outputChest, stoneCutter
        );
    }

    private long workAdded = 0;

    public StoneCutter(Ref<Plot> plot) {
        super(plot, "Stone Cutter");
        scanPlotAsync();
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
//        worker = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Stone Cutter");

//        goal = new BasicWorkerAI(worker, getPlot());
//        worker.getDefaultGoalController().addGoal(goal.executor(), 99);
//
//        worker.spawn(getPlot().getBlock());
//        worker.setProtected(false);
//
//        goal.getWorkBlocks().add(getPlot().getBlock());
    }

    @Override
    public void onDestroy() {
        if (worker != null) {
            worker.destroy();
        }
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        scanPlotAsync();
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        scanPlotAsync();
    }

    private void scanPlotAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(Kingdoms.instance, this::scanPlot);
    }

    private void scanPlot() {
        var plotWorld = getPlot().getBlock().getWorld();
        getAllReqs().forEach(req -> req.setRechecked(false));
        inputInventory.getChests().clear();
        outputInventory.getChests().clear();
        getPlot().getBounds().forEach(((x, y, z, i) -> {
            var blocLoc = new Location(plotWorld, x, y, z);
            var typ = blocLoc.getBlock().getType();
            checkPoi(blocLoc, typ);
        }));
        getAllReqs().forEach(PlotRequirement::ensureRechecked);
        checkEnabled();
    }

    private void checkPoi(Location blocLoc, Material typ) {
        if (Material.STONECUTTER.equals(typ)) {
            stoneCutter.setPoi(blocLoc);
            return;
        }

        if (MaterialUtil.isBed(typ)) {
            bed.setPoi(blocLoc);
            return;
        }

        if (Material.CHEST.equals(typ)) {
            var chestData = CustomBlockData.getBlockData(blocLoc, PlotChest.ChestMetadata.class);
            if (chestData == null) {
                return;
            }

            if (chestData.getTyp() == PlotChest.PlotChestType.INPUT) {
                inputInventory.getChests().add(blocLoc);
                inputChest.setPoi(blocLoc);
            } else if (chestData.getTyp() == PlotChest.PlotChestType.OUTPUT) {
                outputInventory.getChests().add(blocLoc);
                outputChest.setPoi(blocLoc);
            }
        }
    }

    private boolean checkEnabled() {
        for(var req : getAllReqs()) {
            if (!req.isCompleted()) {
                crafting.setEnabled(false);
                return false;
            }
        }
        crafting.setEnabled(true);
        return true;
    }


    @Override
    public ChestGUI getPlotMenu() {
        scanPlot();
        refreshControllerMenu();
        return controllerMenu.get();
    }

    private void refreshControllerMenu() {
        PlotRequirement.updateWithReqs(controllerMenu.get(), getAllReqs());

        controllerMenu.get().setOption(10, CustomItemIndex.INCREASE.toItemStack(), "Current Production", "Current work done: " + workAdded);
        controllerMenu.get().setOption(11, CustomItemIndex.DECREASE.toItemStack(), "Current Consumption");
        controllerMenu.get().refreshAllViewers();
    }

    @Override
    public ChestGUI getCraftingMenu() {
        checkEnabled();
        return crafting.getMenuAndRefresh();
    }

    @Override
    public void tick() {
        if (!checkEnabled()) {
            return;
        }

        tickCrafting();
        crafting.refresh();
        refreshControllerMenu();
    }

    private void tickCrafting() {
        var next = crafting.getNextToBuild();
        if (next == null) {
            workAdded = 0;
            return;
        }

        workAdded++;
        if (workAdded < next.getTimeToCraft()) {
            return;
        }

        crafting.removeNextToBuild(next.getProduct());
        next.getCost().purchase(inputInventory);
        outputInventory.addItem(new ItemStack(next.getProduct()));
        workAdded = 0;
    }
}
