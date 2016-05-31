/*
 * Copyright 2016 Donovan Allen
 *
 * This file is part of Kingdoms for the Morphics Network.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.Kingdoms.Kingdom.Structures.Types;

import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import io.dallen.Kingdoms.Storage.MaterialWrapper;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Allows the kingdom to store raw and refined materials
 * 
 * @author donoa_000
 */
public class Storeroom extends Plot implements Storage{
    @Getter
    private int maxCapacity;
    @Getter
    private BuildingVault Storage;
    
    public Storeroom(Plot p) {
        super(p);
//        maxCapacity = p.getArea() * 100;
        Storage = new BuildingVault(30, 30 * 100, this);
    }
    
    @Override
    public boolean interact(PlayerInteractEvent e){
        if(e.getClickedBlock().getType().equals(Material.CHEST)){
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                if(Storage.CanOpen(e.getPlayer())){
                    Storage.SendToPlayer(e.getPlayer());
                    return true;
                }
            }else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)){
                if(e.hasItem() && this.hasSpace()){
                    Storage.getContents()[Storage.getFullSlots()] = new MaterialWrapper(e.getItem());
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean hasSpace(){
        return Storage.getFullSlots() < Storage.getUniqueSize() && Storage.getAmountFull() < Storage.getCapacity();
    }
    
    @Override
    public boolean supplyNPC(NPC npc){
        return true;
    }
    
    private static class ResourceStats{
        @Getter @Setter
        private int Wealth;
        @Getter @Setter
        private int Grain;
        @Getter @Setter
        private int Sand;
        @Getter @Setter
        private int Wood;
        @Getter @Setter
        private int Ores;
        @Getter @Setter
        private int Stone;
        @Getter @Setter
        private int RefinedWood;
        @Getter @Setter
        private int Brick;
        @Getter @Setter
        private int Metal;
        @Getter @Setter
        private int Corps;
        @Getter @Setter
        private int Glass;
        
        @Getter @Setter
        private int StorageSpace;
        @Getter @Setter
        private int Population;
        
        public ResourceStats(){
            
        }
        
    }
    
}
