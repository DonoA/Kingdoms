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
package io.dallen.kingdoms.core.NPCs.Traits;

import io.dallen.kingdoms.core.Structures.Types.BuildersHut;
import io.dallen.kingdoms.core.NPCs.FiniteStateMachine;
import io.dallen.kingdoms.core.NPCs.FiniteStateMachine.FsmState;
import io.dallen.kingdoms.utilities.Blueprint.BlueBlock;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Donovan Allen
 */
public class Builder extends Trait{
    
    @Getter
    private Inventory buildInventory;
    
    @Getter @Setter
    private BuildersHut BuildHut;
    
    private final FiniteStateMachine brain;
    
    private static HashMap<Integer, Location> gettingSupplies = new HashMap<Integer, Location>();
    
    public Builder(){
        super("Builder");
        brain = new FiniteStateMachine();
    }
    
    public class getSupplies implements FsmState{
        
        private Location target;
        
        private long ticksTilNextPatrol = 0;
        
        @Override
        public void invoke(){
            if(!npc.getNavigator().isNavigating() && ticksTilNextPatrol <= 0){
               
            }else{
                ticksTilNextPatrol--;
            }
        }
        
        @Override
        public boolean isComplete(){
            return false;
        }
    }
    
    public class placeBlock implements FsmState{
        
        private Location target;
        
        private BlueBlock block;
        
        private long ticksTilNextPatrol = 0;
        
        @Override
        public void invoke(){
            if(!npc.getNavigator().isNavigating() && ticksTilNextPatrol <= 0){
               
            }else{
                ticksTilNextPatrol--;
            }
        }
        
        @Override
        public boolean isComplete(){
            return false;
        }
    }
}
