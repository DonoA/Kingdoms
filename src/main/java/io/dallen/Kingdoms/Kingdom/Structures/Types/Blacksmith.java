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
 * Allows the kingdom to manufacture weapons and armor
 * 
 * @author donoa_000
 */
public class Blacksmith extends Plot{
    @Getter
    private BuildingVault stock;
    @Getter
    private int stockCapacity;
    @Getter
    private int workerCapacity;
    @Getter
    private BlacksmithTools tools;

    public Blacksmith(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
    }
    
    @NoArgsConstructor
    private static class BlacksmithTools{ //holds the levels of the tools in the shop
        @Getter
        private int Anvail;
        @Getter
        private int Forge;
        @Getter
        private int Press;
        @Getter
        private int Molder;
    
    }
}
