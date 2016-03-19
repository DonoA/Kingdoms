/*
 * This file is part of Kingdoms.
 * 
 * Kingdom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdom.  If not, see <http://www.gnu.org/licenses/>.
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
