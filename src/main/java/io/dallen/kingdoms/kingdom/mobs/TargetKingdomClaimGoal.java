package io.dallen.kingdoms.kingdom.mobs;

import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.packets.BlockBreakAnimator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
public class TargetKingdomClaimGoal extends BehaviorGoalAdapter {

    private static Random random = new Random();

    private enum State {
        INIT, TARGETING_CLAIM, TARGETING_CLAIM_BLOCK, TARGETING_OBSTRUCTION, STUCK,
            FINISHED
    }

    private final Kingdom targetKingdom;
    private final NPC npc;
    private final float damage;

    private State state = State.INIT;
    private Location targetBlock;

    private int stuckCounter = 0;
    private Location lastLoc = null;

    @Override
    public void reset() {
        state = State.INIT;
        targetBlock = null;
    }

    @Override
    public BehaviorStatus run() {
        if (state == State.FINISHED) {
            return BehaviorStatus.SUCCESS;
        }

        var isStuck = checkStuck();

        switch(state) {
            case INIT:
                state = State.TARGETING_CLAIM;
                break;
            case TARGETING_CLAIM:
                if (isStuck) {
                    state = State.STUCK;
                }
                targetClaim();
                break;
            case TARGETING_OBSTRUCTION:
                if (canHitBlock(targetBlock)) {
                    var broken = hitBlock(targetBlock);
                    if (broken) {
                        state = State.INIT;
                    }
                } else {
                    state = State.INIT;
                }
                break;
            case TARGETING_CLAIM_BLOCK:
                targetBlock = targetKingdom.getBlock();
                if (canHitBlock(targetBlock)) {
                    var broken = hitBlock(targetBlock);
                    if (broken) {
                        targetKingdom.destroy();
                        state = State.FINISHED;
                    }
                } else {
                    state = State.INIT;
                }
                break;
            case STUCK:
                targetBlock = getObstructingBlock();
                if (targetBlock != null) {
                    state = State.TARGETING_OBSTRUCTION;
                } else {
                    state = State.INIT;
                }
                break;

        }

        return BehaviorStatus.RUNNING;
    }

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

    private void targetClaim() {
        var nav = npc.getNavigator();
        if (nav.getTargetAsLocation() != null && targetKingdom.getBlock().equals(nav.getTargetAsLocation())) {
            return;
        }

        nav.setTarget(targetKingdom.getBlock());
        nav.getLocalParameters().addSingleUseCallback((cancelReason) -> {
            if (cancelReason == CancelReason.STUCK) {
                state = State.STUCK;
            } else if (cancelReason == null) { // finished
                state = State.TARGETING_CLAIM_BLOCK;
            }
        });
    }

    private Location getObstructingBlock() {
        if (targetKingdom.getBlock().getBlockY() < npc.getEntity().getLocation().getBlockY()) {
            return npc.getStoredLocation().clone().add(0, -1 ,0);
        }

        var targetVector = targetKingdom.getBlock().clone().subtract(npc.getEntity().getLocation());
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

    @Override
    public boolean shouldExecute() {
        return npc.isSpawned();
    }
}
