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

import io.dallen.kingdoms.core.Contract;
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Structures.Types.BuildersHut;
import io.dallen.kingdoms.core.NPCs.FiniteStateMachine;
import io.dallen.kingdoms.core.NPCs.FiniteStateMachine.FsmState;
import io.dallen.kingdoms.core.Overrides.KingdomMaterial;
import io.dallen.kingdoms.core.Structures.Storage;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.core.Structures.Vaults.BuildingVault;
import io.dallen.kingdoms.utilities.Blueprint;
import io.dallen.kingdoms.utilities.Blueprint.BlueBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class Builder extends Trait {

    @Getter
    private Inventory buildInventory;

    @Getter
    @Setter
    private BuildersHut BuildHut;
    
    @Getter
    private final FiniteStateMachine brain;

    private static HashMap<Integer, Location> gettingSupplies = new HashMap<Integer, Location>();

    public Builder(BuildersHut home) {
        super("Builder");
        this.BuildHut = home;
        brain = new FiniteStateMachine();
    }
    
    public class getSupplies implements FsmState {

        private Location target;
        
        private ArrayList<ItemStack> neededItems;
        
        public getSupplies(ArrayList<ItemStack> neededItems){
            
        }


        @Override
        public void invoke() {
            if (!npc.getNavigator().isNavigating()) {

            } else {
                
            }
        }

        @Override
        public boolean isComplete() {
            return false;
        }
    }

    public class placeBlock implements FsmState {

        private Location target;

        private BlueBlock block;
        
        private boolean hasPath = false;
        
        public placeBlock(Location loc, BlueBlock block){
            this.target = loc;
            this.block = block;
        }

        @Override
        public void invoke() {
            if (!npc.getNavigator().isNavigating()){
                if(!hasPath){
                    npc.getNavigator().setTarget(target);
                    hasPath = true;
                }else{
                    Bukkit.getScheduler().runTask(KingdomsCore.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            target.getBlock().setType(block.getBlock(), true);
                            target.getBlock().setData(block.getData());
                        }
                    });
                }
            }
                
        }

        @Override
        public boolean isComplete() {
            if(hasPath && target.getBlock().getType().equals(block.getBlock())){
                return true;
            }
            return false;
        }
    }
}
