package io.dallen.kingdoms.kingdom.ai;

import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;

public class GoalExecutorBehavior extends BehaviorGoalAdapter {

    private final NpcState initState;

    @Setter
    private NpcState currentState;

    @Setter
    private boolean finished = false;

    public GoalExecutorBehavior(NpcState initState) {
        this.initState = initState;
        this.currentState = initState;
    }

    @Override
    public void reset() {
        currentState = initState;
        finished = false;
    }

    @Override
    public BehaviorStatus run() {
        var nextState = currentState.execute(this);
        if (nextState != null) {
            currentState = nextState;
        }

        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        return !finished;
    }
}
