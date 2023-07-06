package io.dallen.kingdoms.kingdom.ai;

import io.dallen.kingdoms.kingdom.plot.Plot;
import io.dallen.kingdoms.util.TimeUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SleepTrait;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class BasicWorkerAI extends KingdomsAI {

    private final NPC npc;
    private final Plot plot;
    
    @Getter
    private Set<Location> beds = new HashSet<>();
    private Set<Location> unsureBeds = new HashSet<>();
    private Location favBed;
    @Getter
    private Set<Location> workBlocks = new HashSet<>();
    private Location favWorkBlock;

    private long startWaitingTime = -1L;
    private boolean isTired = false;

    @Override
    public GoalExecutorBehavior executor() {
        return new GoalExecutorBehavior(this.initState);
    }

    private final NpcState initState = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            var worldTime = TimeUtil.getTime(npc.getStoredLocation().getWorld());
            if (worldTime.isNight()) {
                return trySleep;
            } else {
                return tryWork;
            }
        }

        @Override
        public String explain() {
            return "Deciding what to do next";
        }
    };

    private final NpcState trySleep = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            if (favBed == null) {
                favBed = findBed();
            }

            if (favBed == null) {
                return tired;
            }

            var bedData = getBedData(favBed.getBlock());
            if (bedData == null || bedData.isOccupied()) {
                unsureBeds.add(favBed);
                favBed = null;
                return tired;
            }

            return goBed;
        }

        @Override
        public String explain() {
            return "Finding a place to sleep";
        }
    };

    private final NpcState tired = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            if (startWaitingTime == -1L) {
                startWaitingTime = System.currentTimeMillis();
            }

            if (startWaitingTime + 30 * 1000 < System.currentTimeMillis()) {
                isTired = true;
                return initState;
            } else {
                return null;
            }
        }

        @Override
        public String explain() {
            return "Cannot find a place to sleep";
        }
    };

    private final NpcState goBed = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            var nav = npc.getNavigator();
            if (nav.getTargetAsLocation() != null && favBed.equals(nav.getTargetAsLocation())) {
                return null;
            }

            nav.setTarget(favBed);
            nav.getLocalParameters().addSingleUseCallback((cancelReason) -> {
                if (cancelReason == CancelReason.STUCK) {
                    unsureBeds.add(favBed);
                    executor.setCurrentState(tired);
                } else if (cancelReason == null) { // finished
                    executor.setCurrentState(sleep);
                }
            });

            return null;
        }

        @Override
        public String explain() {
            return "Heading to bed";
        }
    };

    private final NpcState sleep = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            var worldTime = TimeUtil.getTime(npc.getStoredLocation().getWorld());
            if (!worldTime.isNight()) {
                npc.getOrAddTrait(SleepTrait.class).setSleeping(null);
                return initState;
            }

            // lie down in bed
            npc.getOrAddTrait(SleepTrait.class).setSleeping(favBed);
            isTired = false;

            return null;
        }

        @Override
        public String explain() {
            return "Sleeping";
        }
    };

    private final NpcState tryWork = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            return initState;
        }

        @Override
        public String explain() {
            return "Finding work";
        }
    };

    //Helpers
    private Location findBed() {
        for (var bedLoc : beds) {
            if (unsureBeds.contains(bedLoc)) {
                continue;
            }

            var blocData = bedLoc.getBlock().getBlockData();
            if (!(blocData instanceof Bed)) {
                continue;
            }
            return bedLoc;
        }

        unsureBeds.clear();
        return null;
    }

    private Bed getBedData(Block block) {
        var data = block.getBlockData();
        if (!(data instanceof Bed)) {
            return null;
        }

        return (Bed) data;
    }
}
