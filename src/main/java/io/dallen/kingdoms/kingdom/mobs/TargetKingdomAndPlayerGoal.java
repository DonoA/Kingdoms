package io.dallen.kingdoms.kingdom.mobs;

import io.dallen.kingdoms.kingdom.Kingdom;
import io.dallen.kingdoms.packets.BlockBreakAnimator;
import lombok.RequiredArgsConstructor;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class TargetKingdomAndPlayerGoal extends BehaviorGoalAdapter {

    private enum State {
        INIT, TARGETING_PLAYER, TARGETING_CLAIM, TARGETING_CLAIM_BLOCK, TARGETING_OBSTRUCTION, STUCK,
            FINISHED
    }

    private final Kingdom targetKingdom;
    private final NPC npc;
    private final int playerTargetRadius;
    private final long retargetTime;
    private final float damage;

    private State state = State.INIT;
    private Player targetPlayer;
    private Location targetBlock;
    private long stuckTime;

    private int stuckCounter = 0;
    private Location lastLoc = null;

    @Override
    public void reset() {
        state = State.INIT;
        targetPlayer = null;
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
                state = State.TARGETING_PLAYER;
                targetPlayer = getPlayer(playerTargetRadius);
                break;
            case TARGETING_PLAYER:
                if (isStuck) {
                    state = State.TARGETING_CLAIM;
                    stuckTime = System.currentTimeMillis();
                    break;
                }

                if (canTargetPlayer(targetPlayer, playerTargetRadius)) {
                    setTargetPlayer(targetPlayer);
                } else {
                    state = State.TARGETING_CLAIM;
                }
                break;
            case TARGETING_CLAIM:
                targetClaim();
                break;
            case TARGETING_OBSTRUCTION:
                var player = getPlayer(playerTargetRadius);
                if (canTargetPlayer(player, playerTargetRadius) && stuckTimerExpired(retargetTime)) {
                    setTargetPlayer(targetPlayer);
                    state = State.TARGETING_PLAYER;
                    break;
                }

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

    private boolean stuckTimerExpired(long requiredElapsed) {
        return stuckTime + requiredElapsed < System.currentTimeMillis();
    }

    private boolean canHitBlock(Location targetBlock) {
        var npcLoc = npc.getStoredLocation();
        return targetBlock.distance(npcLoc) < 2;
    }

    private boolean hitBlock(Location targetBlock) {
        for (var player : Bukkit.getOnlinePlayers()) {
            player.playSound(targetBlock, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1f, 1f);
        }
        return BlockBreakAnimator.doDamage(targetBlock, damage, false);
    }

    private void targetClaim() {
        var nav = npc.getNavigator();
        if (nav.getTargetAsLocation() != null && targetKingdom.getBlock().equals(nav.getTargetAsLocation())) {
            return;
        }

        nav.setTarget(targetKingdom.getBlock());
        nav.getLocalParameters().addSingleUseCallback((cancelReason) -> {
            if (cancelReason == CancelReason.STUCK) {
                stuckTime = System.currentTimeMillis();
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
        var offsetX = targetVector.getBlockX() / Math.abs(targetVector.getBlockX());
        var offsetZ = targetVector.getBlockZ() / Math.abs(targetVector.getBlockZ());

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

    private void setTargetPlayer(@NotNull Player player) {
        var nav = npc.getNavigator();
        if (nav.getEntityTarget() != null && player.equals(nav.getEntityTarget().getTarget())) {
            return;
        }

        nav.setTarget(player, true);
        nav.getLocalParameters().addSingleUseCallback((cancelReason) -> {
             if (cancelReason == CancelReason.STUCK) {
                stuckTime = System.currentTimeMillis();
                state = State.STUCK;
            }
        });
    }

    private Player getPlayer(int radius) {
        for(var player : Bukkit.getOnlinePlayers()) {
            var npcLoc = npc.getStoredLocation();
            if (player.getLocation().distance(npcLoc) > radius) {
                continue;
            }

            return player;
        }

        return null;
    }

    private boolean canTargetPlayer(Player player, int radius) {
        if (player == null) {
            return false;
        }

        var canNav = npc.getNavigator().canNavigateTo(player.getLocation());
        if (!canNav) {
            return false;
        }

        var npcLoc = npc.getStoredLocation();
        return !(player.getLocation().distance(npcLoc) > radius);
    }

    @Override
    public boolean shouldExecute() {
        return npc.isSpawned();
    }
}
