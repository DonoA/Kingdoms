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

import io.dallen.Kingdoms.Util.LocationUtil;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;

/**
 *
 * @author donoa_000
 */
public class WallSystem{
    
    @Getter
    private HashMap<WallType, ArrayList<Wall>> Parts = new HashMap<WallType, ArrayList<Wall>>();
    
    @Getter
    private Municipality municipal;
    
    WallSystem(Municipality m) {
        this.municipal = m;
    }
    
    public void recalculateBase(){
        ArrayList<Wall> corners = new ArrayList<Wall>();
        corners.addAll(Parts.get(WallType.CORNER));
        corners.addAll(Parts.get(WallType.TOWER));
        int[] Xs = new int[corners.size()];
        int[] Zs = new int[corners.size()];
        int i = 0;
        for(i = 0; i < corners.size(); i++){
            Xs[i] = (int) LocationUtil.asPoint(corners.get(i).getCenter()).getX();
            Zs[i] = (int) LocationUtil.asPoint(corners.get(i).getCenter()).getY();
            //test that there is some kind of plot connecting this point and the next point
        }
        Polygon newBase = new Polygon(Xs, Zs, i);
        
        municipal.setBase(newBase);
    }
    
    public static enum WallType {
        WALL, GATE, TOWER, CORNER
    }
    
    public static class Wall extends Plot{
        @Getter
        private WallType type;
        
        public Wall(Plot p, WallType type){
            super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
            this.type = type;
        }
    }
    
}
