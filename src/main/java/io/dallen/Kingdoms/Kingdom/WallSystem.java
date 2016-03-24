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
package io.dallen.Kingdoms.Kingdom;

import io.dallen.Kingdoms.Util.LocationUtil;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Location;

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
    
    public boolean recalculateBase(){//Should be called async if possible
        ArrayList<Wall> corners = new ArrayList<Wall>();
        corners.addAll(Parts.get(WallType.CORNER));
        corners.addAll(Parts.get(WallType.TOWER));
        int[] Xs = new int[corners.size()];
        int[] Zs = new int[corners.size()];
        int i = 0;
        for(i = 0; i < corners.size(); i++){
            Xs[i] = (int) LocationUtil.asPoint(corners.get(i).getCenter()).getX();
            Zs[i] = (int) LocationUtil.asPoint(corners.get(i).getCenter()).getY();
            boolean found = false;
            if(i+1<corners.size()){
                Point a = LocationUtil.asPoint(corners.get(i).getCenter());
                Point b = LocationUtil.asPoint(corners.get(i+1).getCenter());
                if(a.getX() == b.getX()){//If the Xs are the same, change Z
                    for(int z = (int) a.getY(); z<b.getY() && !found; z++){
                        Location l = new Location(corners.get(i).getCenter().getWorld(), a.getX(), corners.get(i).getCenter().getBlockY(), z);
                        Plot p = Plot.inPlot(l);
                        if(p instanceof Wall){
                            found = true;
                        }
                    }
                }else if(a.getY() == b.getY()){//If the Zs are the same, change X
                    for(int x = (int) a.getX(); x<b.getX(); x++){
                        Location l = new Location(corners.get(i).getCenter().getWorld(), x, corners.get(i).getCenter().getBlockY(), a.getY());
                        Plot p = Plot.inPlot(l);
                        if(p instanceof Wall){
                            found = true;
                        }
                    }
                }
            }else{
                found = true;
            }
            if(!found){
                return false;
            }
        }
        Polygon newBase = new Polygon(Xs, Zs, i);
        municipal.setBase(newBase);
        return true;
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
