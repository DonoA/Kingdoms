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
package io.dallen.kingdoms.utilities.Utils;

import java.awt.Point;
import java.awt.Polygon;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author donoa_000
 */
public class LocationUtil {
    
    public static Point asPoint(Location l){
        return new Point(l.getBlockX(), l.getBlockZ());
    }
    
    public static Location asLocation(Point p, World w, int Ycord){
        return new Location(w, p.getX(), Ycord, p.getY());
    }
    
    public static Point calcCenter(Point[] corners){
        long X = 0;
        long Y = 0;
        for(int i = 0; i<corners.length; i++){
            X+=corners[i].getX();
            Y+=corners[i].getY();
        }
        X/=corners.length;
        Y/=corners.length;
        return new Point((int) X, (int) Y);
    }
}
