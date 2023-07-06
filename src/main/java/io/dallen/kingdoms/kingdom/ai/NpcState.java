package io.dallen.kingdoms.kingdom.ai;

public interface NpcState {
    NpcState execute(GoalExecutorBehavior executor);
}
