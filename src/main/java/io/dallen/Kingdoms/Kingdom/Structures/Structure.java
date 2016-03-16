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
package io.dallen.Kingdoms.Kingdom.Structures;

import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import java.awt.Polygon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class Structure {
    
    @Getter @Setter
    private int Width; // X
    @Getter @Setter
    private int Height; // Y
    @Getter @Setter
    private int Length; // Z
    @Getter @Setter
    private Polygon Base; // Z
    @Getter @Setter
    private Location Center;
    @Getter @Setter
    private int ID;
    @Getter @Setter
    private Player Owner;
    @Getter @Setter
    private Kingdom Kingdom;
    @Getter @Setter
    private Municipality Municipal;
    
    public Structure(Polygon base, Location cent, Player own, Kingdom kingdom, Municipality municipal){
        this.Center = cent;
        this.Owner = own;
        this.Kingdom = kingdom;
        this.Municipal = municipal;
        this.Base = base;
    }
    
    public Structure(Polygon base, Location cent, Player own, Municipality municipal){
        this.Base = base;
        this.Center = cent;
        this.Owner = own;
        this.Municipal = municipal;
    }
    
    public Structure(Polygon base, Location cent, Player own, Kingdom kingdom){
        this.Base = base;
        this.Center = cent;
        this.Owner = own;
        this.Kingdom = kingdom;
    }
    
}
