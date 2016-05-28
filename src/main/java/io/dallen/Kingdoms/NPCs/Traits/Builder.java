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
package io.dallen.Kingdoms.NPCs.Traits;

import io.dallen.Kingdoms.Kingdom.Structures.Types.BuildersHut;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    
    private static HashMap<Integer, Location> gettingSupplies = new HashMap<Integer, Location>();
    
    public Builder(){
        super("Builder");
    }
    
    public void getSupplies(Location rtnLoc){
        super.npc.getNavigator().setTarget(BuildHut.getCenter());
        gettingSupplies.put(super.npc.getId(), rtnLoc);
    }
    
    public static class navigationEvents implements Listener{
        
        @EventHandler
        public void onNavigationComplete(NavigationCompleteEvent e){
            if(gettingSupplies.containsKey(e.getNPC().getId())){
                e.getNavigator().setTarget(gettingSupplies.get(e.getNPC().getId()));
            }
        }
    }
}
