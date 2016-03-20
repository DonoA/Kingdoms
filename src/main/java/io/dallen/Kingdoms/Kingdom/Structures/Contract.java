/*
 * This file is part of Kingdoms.
 * 
 * Kingdoms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdoms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdoms.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms.Kingdom.Structures;

import lombok.Getter;
import org.bukkit.Material;
import lombok.AllArgsConstructor;

/**
 *
 * @author donoa_000
 */
@AllArgsConstructor
public class Contract {
    @Getter
    private String Message; 
    
    @Getter
    private Class Type;
    
    @Getter
    private int pay;
    
    @Getter
    private Material[] requestedMaterials;
}
