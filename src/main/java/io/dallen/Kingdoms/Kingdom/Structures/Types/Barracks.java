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
import lombok.NoArgsConstructor;

/**
 * Allows the kingdom to house its NPCs
 * 
 * @author donoa_000
 */
public class Barracks extends Plot{

    @Getter
    private int maxCapacity;
    @Getter
    private int currentCapacity;
    @Getter
    private int amountFull;
    
    @Getter
    private int readySpeed;
    
    @Getter
    private PopulationStats people;
    
    public Barracks(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
        maxCapacity = p.getArea() * 100;
        currentCapacity = 0;
        amountFull = 0;
        people = new PopulationStats();
        readySpeed = 10;
    }

    @NoArgsConstructor
    public static class PopulationStats{
        @Getter
        private int infantry;
        
        @Getter
        private int calvalry;
        
        @Getter
        private int archers;
        
        @Getter
        private int other;
    }
}
