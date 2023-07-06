package io.dallen.kingdoms.kingdom.ai;

import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.packets.BlockBreakAnimator;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class TargetClaimAI extends KingdomsAI {

    private static Random random = new Random();

    private final Kingdom targetKingdom;
    private final NPC npc;
    private final float damage;

    private int stuckCounter = 0;
    private Location lastLoc = null;
    private Location targetBlock = null;

    @Override
    public GoalExecutorBehavior executor() {
        return new GoalExecutorBehavior(initState);
    }

    // States
    private final NpcState initState = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            return targetClaim;
        }

        @Override
        public String explain() {
            return "Preparing to attack";
        }
    };

    private final NpcState targetClaim = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
//        if (checkStuck()) {
//            return this::targetObstruction;
//        }

            var nav = npc.getNavigator();
            if (nav.getTargetAsLocation() != null && targetKingdom.getBlock().equals(nav.getTargetAsLocation())) {
                return null;
            }

            nav.setTarget(targetKingdom.getBlock());
            nav.getLocalParameters().addSingleUseCallback((cancelReason) -> {
                if (cancelReason == CancelReason.STUCK) {
                    executor.setCurrentState(targetObstruction);
                } else if (cancelReason == null) { // finished
                    executor.setCurrentState(targetClaimBlock);
                }
            });
            return null;
        }

        @Override
        public String explain() {
            return "Targeting claim";
        }
    };

    private final NpcState targetClaimBlock = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            targetBlock = targetKingdom.getBlock();

            if (canHitBlock(targetBlock)) {
                var broken = hitBlock(targetBlock);
                if (broken) {
                    targetKingdom.destroy();
                    executor.setFinished(true);
                }
            } else {
                return initState;
            }

            return null;
        }

        @Override
        public String explain() {
            return "Attacking claim block";
        }
    };

    private final NpcState targetObstruction = new NpcState() {
        @Override
        public NpcState execute(GoalExecutorBehavior executor) {
            if (targetBlock == null) {
                targetBlock = getObstructingBlock();
            }

            if (canHitBlock(targetBlock)) {
                var broken = hitBlock(targetBlock);
                if (broken) {
                    return initState;
                }
            } else {
                return initState;
            }

            return null;
        }

        @Override
        public String explain() {
            return "Breaking obstructing block";
        }
    };

    // Helpers
    private boolean checkStuck() {
        var currentLoc = npc.getStoredLocation();
        if (lastLoc == null) {
            lastLoc = currentLoc;
            return false;
        }

        if (lastLoc.getBlock().equals(currentLoc.getBlock())) {
            stuckCounter++;
        }
        lastLoc = currentLoc;

        if (stuckCounter > 50) {
            stuckCounter = 0;
            lastLoc = null;
            return true;
        } else {
            return false;
        }
    }

    private boolean canHitBlock(Location targetBlock) {
        if (targetBlock == null) {
            return false;
        }

        var npcLoc = npc.getStoredLocation();
        return targetBlock.distance(npcLoc) < 2;
    }

    private boolean hitBlock(Location targetBlock) {
        var blockBroken = BlockBreakAnimator.doDamage(targetBlock, damage, false);

        if (blockBroken || random.nextInt(100) <= 10) {
            for (var player : Bukkit.getOnlinePlayers()) {
                player.playSound(targetBlock, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1f, 1f);
            }
        }
        return blockBroken;
    }

    private Location getObstructingBlock() {
        if (targetKingdom.getBlock().getBlockY() < npc.getStoredLocation().getBlockY()) {
            return npc.getStoredLocation().clone().add(0, -1 ,0);
        }

        var targetVector = targetKingdom.getBlock().clone().subtract(npc.getStoredLocation());
        var offsetX = targetVector.getBlockX() == 0 ? 0 : targetVector.getBlockX() / Math.abs(targetVector.getBlockX());
        var offsetZ = targetVector.getBlockZ() == 0 ? 0 : targetVector.getBlockZ() / Math.abs(targetVector.getBlockZ());

        var testLocations = List.of(
                npc.getStoredLocation().clone().add(offsetX, 1 ,0),
                npc.getStoredLocation().clone().add(offsetX, 0 ,0),
                npc.getStoredLocation().clone().add(0, 1 ,offsetZ),
                npc.getStoredLocation().clone().add(0, 0 ,offsetZ)
        );

        for (var testLoc : testLocations) {
            if (testLoc.getBlock().getType().isSolid()) {
                return testLoc;
            }
        }

        return null;
    }

}
