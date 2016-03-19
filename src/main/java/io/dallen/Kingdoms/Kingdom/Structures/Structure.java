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

import com.google.common.primitives.Ints;
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Util.LogUtil;
import java.awt.Point;
import java.awt.Polygon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
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
    @Getter @Setter
    private int Area;
    
    public Structure(Polygon base, Location cent, Player own, Kingdom kingdom, Municipality municipal){
        this.Center = cent;
        this.Owner = own;
        this.Kingdom = kingdom;
        this.Municipal = municipal;
        this.Base = base;
        setArea();
    }
    
    public Structure(Polygon base, Location cent, Player own, Municipality municipal){
        this.Base = base;
        this.Center = cent;
        this.Owner = own;
        this.Municipal = municipal;
        setArea();
    }
    
    public Structure(Polygon base, Location cent, Player own, Kingdom kingdom){
        this.Base = base;
        this.Center = cent;
        this.Owner = own;
        this.Kingdom = kingdom;
        setArea();
    }
    
    private void setArea(){
        int Xmax = Ints.max(Base.xpoints);
        int Zmax = Ints.max(Base.ypoints);
        for(int x = Ints.min(Base.xpoints); x <= Xmax; x++){
            for(int z = Ints.min(Base.ypoints); z <= Zmax; z++){
                if(Base.contains(new Point(x,z)) || (Base.contains(new Point(x-1,z)) || Base.contains(new Point(x,z-1)) || Base.contains(new Point(x-1,z-1)))){
                    Area++;
                }
            }
        }
    }
    
}
