/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of Kingdoms for the Morphics Network.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.kingdoms.core.NPCs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

/**
 *
 * @author Donovan Allen
 */
public class FiniteStateMachine {

    @Getter
    @Setter
    private Queue<FsmState> stateQueue = new LinkedList<FsmState>();

    private FsmState defaultState = null;

    @Getter
    private static ArrayList<FiniteStateMachine> brains = new ArrayList<>();

    public FiniteStateMachine() {
        brains.add(this);
    }

    public FiniteStateMachine(FsmState defaultState) {
        this.defaultState = defaultState;
        brains.add(this);
    }

    public void update() {
        if (!stateQueue.isEmpty()) {
            if (stateQueue.peek().isComplete()) {
                stateQueue.remove();
            }
            stateQueue.peek().invoke();
        } else if (defaultState != null) {
            defaultState.invoke();
        }
    }

    public static interface FsmState {

        public void invoke();

        public boolean isComplete();
    }

    public static class basicNavigation implements FsmState {

        private boolean assignedNav = false;

        private Location target;

        private NPC npc;

        public basicNavigation(Location target, NPC npc) {
            this.target = target;
            this.npc = npc;
        }

        @Override
        public void invoke() {
            if (!assignedNav && !npc.getNavigator().isNavigating()) {
                npc.getNavigator().setTarget(target);
                assignedNav = true;
            }
        }

        @Override
        public boolean isComplete() {
            return assignedNav && !npc.getNavigator().isNavigating();
        }
    }
}
