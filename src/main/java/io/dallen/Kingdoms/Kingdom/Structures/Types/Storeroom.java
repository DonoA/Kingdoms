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
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import lombok.Getter;
import lombok.Setter;

/**
 * Allows the kingdom to store raw and refined materials
 * 
 * @author donoa_000
 */
public class Storeroom extends Plot{
    @Getter
    private int maxCapacity;
    @Getter
    private int currentCapacity;
    @Getter
    private int amountFull;
    
    @Getter
    private ResourceStats stats;
    
    @Getter
    private BuildingVault storage;
    
    public Storeroom(Plot p) {
        super(p);
        maxCapacity = p.getArea() * 100;
        currentCapacity = 0;
        amountFull = 0;
        stats = new ResourceStats();
        storage = new BuildingVault(0);
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
