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
