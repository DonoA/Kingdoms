/*
 * This file is part of Kingdom.
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
package io.dallen.Kingdom.Structures;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 *
 * @author donoa_000
 */
public class Structure {
    
    @Getter @Setter
    private int Width;
    @Getter @Setter
    private int Height;
    @Getter @Setter
    private int Length;
    @Getter @Setter
    private Location Center;
    @Getter @Setter
    private int ID;
    @Getter @Setter
    private String Owner;
    @Getter @Setter
    private String Faction;
    
    public Structure(int w, int l, int h, Location cent, String own, String fac){
        
    }
    
}
