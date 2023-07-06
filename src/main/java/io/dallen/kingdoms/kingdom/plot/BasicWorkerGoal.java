package io.dallen.kingdoms.kingdom.plot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.block.data.type.Bed;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class BasicWorkerGoal extends BehaviorGoalAdapter {

    private final NPC npc;
    private final Plot plot;
    @Getter
    private Set<Location> beds = new HashSet<>();
    private Location favBed;
    @Getter
    private Set<Location> workBlocks = new HashSet<>();
    private Location favWorkBlock;

    private State state;
    private enum State {
        INIT, TIRED, CANNOT_SLEEP, FINDING_BED, SLEEPING, WORKING, IDLE
    }


    @Override
    public void reset() {

    }

    @Override
    public BehaviorStatus run() {
        var worldTime = npc.getStoredLocation().getWorld().getTime();
        if (worldTime > 0 && worldTime < 12_000) {
            doSleep();
        } else {
            doWork();
        }

        return BehaviorStatus.RUNNING;
    }

    private void doSleep() {
        if (state == State.FINDING_BED) {
            return;
        }

        if (beds.isEmpty()) {
            state = State.TIRED;
            return;
        }

        if (favBed == null) {
            favBed = findBed();
        }

        if (favBed == null) {
            state = State.CANNOT_SLEEP;
            return;
        }

        state = State.FINDING_BED;
        npc.getNavigator().setTarget(favBed);
//        npc.getNavigator().
    }

    private Location findBed() {
        for (var bedLoc :  beds) {
            var blocData = bedLoc.getBlock().getBlockData();
            if (!(blocData instanceof Bed)) {
                continue;
            }
            var bedData = (Bed) blocData;

            if (!bedData.isOccupied()) {
                return bedLoc;
            }
        }

        return null;
    }

    private void doWork() {

    }

    @Override
    public boolean shouldExecute() {
        return npc.isSpawned();
    }
}
