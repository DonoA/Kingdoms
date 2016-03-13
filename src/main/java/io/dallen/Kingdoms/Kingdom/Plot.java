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
package io.dallen.Kingdoms.Kingdom;

import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import java.util.ArrayList;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author donoa_000
 */
public class Plot extends Structure implements Listener{
    
    @Getter
    private static ArrayList<Plot> allPlots = new ArrayList<Plot>();
    
    public Plot(int w, int l, int h, Location cent, Player own, Municipality mun) {
        super(w, l, h, cent, own, mun);
    }
    
    public static Plot inPlot(Location l){
        for(Plot p : allPlots){
            if(l.getBlockX() > p.getCenter().getBlockX() - p.getWidth()/2 && 
               l.getBlockX() < p.getCenter().getBlockX() + p.getWidth()/2 &&
               l.getBlockZ() > p.getCenter().getBlockZ() - p.getLength()/2 && 
               l.getBlockZ() < p.getCenter().getBlockZ() + p.getLength()/2){
                return p;
            }
        }
        return null;
    }
    
    public boolean createMucicpal(){
        if(super.getKingdom() != null){
            return false;
        }
        super.setMunicipal(new Municipality((Structure) this));
        super.getKingdom().getMunicipals().add(super.getMunicipal());
        return true;
    }
    
}
