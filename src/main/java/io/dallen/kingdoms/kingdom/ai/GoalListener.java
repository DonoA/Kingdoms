package io.dallen.kingdoms.kingdom.ai;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GoalListener implements Listener {

    @EventHandler
    public void onInteract(NPCRightClickEvent event) {
        for (var goal : event.getNPC().getDefaultGoalController()) {
            if (!(goal instanceof GoalExecutorBehavior)) {
                continue;
            }

            var goalExecutor = (GoalExecutorBehavior) goal;
            var explainText = goalExecutor.getCurrentState().explain();
            event.getClicker().sendMessage(event.getNPC().getName() + " is " + explainText);
        }
    }

}
