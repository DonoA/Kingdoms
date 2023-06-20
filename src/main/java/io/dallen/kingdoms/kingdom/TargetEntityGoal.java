package io.dallen.kingdoms.kingdom;

import lombok.Builder;
import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.Set;

public class TargetEntityGoal extends BehaviorGoalAdapter {
    private final boolean aggressive;
    private final Set<EntityType> targets;
    private final NPC npc;
    private final double radius;


    private boolean finished;
    private CancelReason reason;
    private Entity target;

    @Builder
    private TargetEntityGoal(NPC npc, Set<EntityType> targets, boolean aggressive, double radius) {
        this.npc = npc;
        this.targets = targets;
        this.aggressive = aggressive;
        this.radius = radius;
    }

    public void reset() {
        this.npc.getNavigator().cancelNavigation();
        this.target = null;
        this.finished = false;
        this.reason = null;
    }

    public BehaviorStatus run() {
        if (this.finished) {
            return this.reason == null ? BehaviorStatus.SUCCESS : BehaviorStatus.FAILURE;
        }

        if (target == null) {
            target = findTarget();
        }

        var nav = npc.getNavigator();
        nav.setTarget(this.target, this.aggressive);
        nav.getLocalParameters().addSingleUseCallback((cancelReason) -> {
            reason = cancelReason;
            finished = true;
        });

        return BehaviorStatus.RUNNING;
    }

    private Entity findTarget() {
        Collection<Entity> nearby = npc.getEntity().getNearbyEntities(this.radius, this.radius, this.radius);
        for(var possible : nearby) {
            if (targets.contains(possible.getType())) {
                return possible;
            }
        }

        return null;
    }

    public boolean shouldExecute() {
        if (targets.size() == 0 || !npc.isSpawned()) {
            return false;
        }

        target = findTarget();
        return target != null;
    }
}
