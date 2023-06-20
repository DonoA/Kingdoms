package io.dallen.kingdoms.kingdom;

import lombok.Builder;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;

@Builder
public class TargetKingdomClaimGoal extends BehaviorGoalAdapter {

    private final Kingdom targetKingdom;
    private final NPC npc;

    private boolean finished = false;
    private CancelReason reason = null;

    @Override
    public void reset() {

    }

    @Override
    public BehaviorStatus run() {
        if (this.finished) {
            return this.reason == null ? BehaviorStatus.SUCCESS : BehaviorStatus.FAILURE;
        }

        var nav = npc.getNavigator();
        nav.setTarget(targetKingdom.getClaim());
        nav.getLocalParameters().addSingleUseCallback((cancelReason) -> {
            reason = cancelReason;
            finished = true;
        });

        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.npc.isSpawned()) {
            return false;
        }

        return true;
    }
}
