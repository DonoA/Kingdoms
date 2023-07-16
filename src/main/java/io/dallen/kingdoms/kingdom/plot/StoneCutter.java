package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.customblocks.CustomBlockData;
import io.dallen.kingdoms.customblocks.CustomBlockIndex;
import io.dallen.kingdoms.customblocks.blocks.PlotChest;
import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.menus.ChestGUI;
import io.dallen.kingdoms.menus.OptionCost;
import io.dallen.kingdoms.savedata.Ref;
import io.dallen.kingdoms.util.MaterialUtil;
import io.dallen.kingdoms.util.OfflinePlayerUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StoneCutter extends PlotController {


    @RequiredArgsConstructor @Getter
    public static class PlotRequirement {
        private final String name;

        private boolean completed = false;
        private Location poi = null;

        @Setter
        private boolean rechecked = false;

        public void setPoi(Location newPoi) {
            if (newPoi == null) {
                completed = false;
            } else {
                rechecked = true;
                completed = true;
            }
            poi = newPoi;
        }
    }

    private final static Material floorMaterial = Material.COBBLESTONE;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

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

    public StoneCutter(Ref<Plot> plot) {
        super(plot, "Stone Cutter");
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
        worker = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Stone Cutter");

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
        CompletableFuture.runAsync(this::scanPlot);
        event.getPlayer().sendMessage("Scanning");
//        var blockData = event.getBlockPlaced().getBlockData();
//        if (blockData instanceof Bed) {
//            goal.getBeds().add(event.getBlock().getLocation());
//            event.getPlayer().sendMessage("Added bed to stone cutter");
//        }
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
//        var blockData = event.getBlock().getBlockData();
//        if (blockData instanceof Bed) {
//            goal.getBeds().remove(event.getBlock().getLocation());
//            event.getPlayer().sendMessage("Removed bed from stone cutter");
//        }
    }

    public void onBuildingUpdate() {

    }

    private void scanPlot() {
        var owner = getPlot().getKingdom().getOwner();
        var plotWorld = getPlot().getBlock().getWorld();
        getAllReqs().forEach(req -> req.setRechecked(false));
        OfflinePlayerUtil.trySendMessage(owner, "Starting plot scan");
        getPlot().getBounds().forEach(((x, y, z, i) -> {
            var blocLoc = new Location(plotWorld, x, y, z);
            var typ = blocLoc.getBlock().getType();
            checkPoi(blocLoc, typ);
        }));
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
                inputChest.setPoi(blocLoc);
            } else if (chestData.getTyp() == PlotChest.PlotChestType.OUTPUT) {
                outputChest.setPoi(blocLoc);
            }
        }
    }

    @Override
    public ChestGUI getMenu() {
        var gui = new ChestGUI("Stone Cutter", 18);
        for (int i = 0; i < getAllReqs().size(); i++) {
            var req = getAllReqs().get(i);
            if (req.isCompleted()) {
                gui.setOption(i, CustomItemIndex.SUBMIT.toItemStack(), req.getName(), "Completed!");
            } else {
                gui.setOption(i, CustomItemIndex.CANCEL.toItemStack(), req.getName(), "Incomplete!");
            }
        }
        gui.setOption(9, CustomItemIndex.RECYCLE.toItemStack(), "Change plot type");
        gui.setOption(10, CustomItemIndex.INCREASE.toItemStack(), "Current Production");
        gui.setOption(11, CustomItemIndex.DECREASE.toItemStack(), "Current Consumption");
        return gui;
    }
}
