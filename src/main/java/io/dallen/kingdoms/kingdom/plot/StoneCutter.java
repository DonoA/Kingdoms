package io.dallen.kingdoms.kingdom.plot;

import io.dallen.kingdoms.kingdom.ai.BasicWorkerAI;
import io.dallen.kingdoms.menus.OptionCost;
import io.dallen.kingdoms.savedata.Ref;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class StoneCutter extends PlotController {

    private final static Material floorMaterial = Material.COBBLESTONE;
    public static OptionCost getCost(Plot plot) {
        return OptionCost.builder()
                .requirement(floorMaterial, plot.getBounds().getSizeX() * plot.getBounds().getSizeZ())
                .build();
    }

    private NPC worker;
    private BasicWorkerAI goal;

    public StoneCutter(Ref<Plot> plot) {
        super(plot);
    }

    @Override
    public void onCreate() {
        getPlot().setFloor(floorMaterial);
        worker = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Stone Cutter");

        goal = new BasicWorkerAI(worker, getPlot());
        worker.getDefaultGoalController().addGoal(goal.executor(), 99);

        worker.spawn(getPlot().getBlock());
        worker.setProtected(false);

        goal.getWorkBlocks().add(getPlot().getBlock());
    }

    @Override
    public void onDestroy() {
        if (worker != null) {
            worker.destroy();
        }
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        var blockData = event.getBlockPlaced().getBlockData();
        if (blockData instanceof Bed) {
            goal.getBeds().add(event.getBlock().getLocation());
            event.getPlayer().sendMessage("Added bed to stone cutter");
        }
    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        var blockData = event.getBlock().getBlockData();
        if (blockData instanceof Bed) {
            goal.getBeds().remove(event.getBlock().getLocation());
            event.getPlayer().sendMessage("Removed bed from stone cutter");
        }
    }

}
