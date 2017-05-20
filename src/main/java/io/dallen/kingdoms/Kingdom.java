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
package io.dallen.kingdoms;

import io.dallen.utils.LogUtil;
import org.bukkit.Location;
import org.bukkit.Material;


/**
 *
 * @author donoa_000
 */
public class Kingdom{
    
    private Location center;
    
    public Kingdom(Location cent){
        this.center = cent;
    }
    
    public void build(){
        // A few constants so that I can quickly tweek things
        double A = 16*6; // 16:9 seems like a good ratio
        double B = 9*6;
        double HeightScale = 13.0;
        // (X-h)^2/a^2 + (Y-k)^2/b^2 = 1
        double A2 = A*A;
        double B2 = B*B;
        Location c1 = center.clone();
        c1.add(-A, 0, -B);
        Location c2 = center.clone();
        c2.add(A, 0, B);
        LogUtil.printDebug("Scan c1 " + c1.toString());
        LogUtil.printDebug("Scan c2 " + c2.toString());
        // The goal here is to generate an eliptical wall structure
        // scan rectangle
        for(int x = c1.getBlockX()-1; x <= c2.getBlockX()+1; x++){
            double zScale = 0;
            for(int z = c1.getBlockZ()-1; z <= c2.getBlockZ()+1; z++){
                Location c = new Location(center.getWorld(), x, center.getBlockY(), z);
                c.getBlock().setType(Material.STONE); // show scanned area
                double func = (Math.pow(x-center.getBlockX(), 2)/A2)+(Math.pow(z-center.getBlockZ(), 2)/B2);
                
                if(Math.abs(func - 1.0) < 0.05){ // generate wall
                    zScale = Math.PI/(2.0*Math.sqrt(c.distanceSquared(center)-Math.pow(x-center.getBlockX(), 2)));
                    for(int y = 0; y < 7; y++){
                        c.clone().add(0, y, 0).getBlock().setType(Material.COBBLESTONE); // set wall
                    }
                }else if(func < 1.0){ // interior
                    // Fill it with hill that is sharp on one side
                    
                    // 1/2*sin(Ax/3)*(Ax+3sin(Ax)/2) in x direction 0 - 3pi domain should cover longest possible x len, scale with A
                    // 2/3*sin(Ax) in z direction 0 - pi domain should cover hight of ellipse scale with A
                    
                    double xScale = (3*Math.PI)/(2*A);
                    double absX = xScale*(x - c1.getBlockX());
                    
                    LogUtil.printDebug("xScale: " + xScale);
                    
                    double absZ = zScale*(Math.abs(z - c1.getBlockZ())-B);
                    
                    LogUtil.printDebug("zScale: " + zScale);
                    
                    int yMax = (int) Math.floor(HeightScale*(0.5*Math.sin(absX/3.0)*(absX+1.5*Math.sin(absX)))*((2.0/3.0)*Math.cos(absZ)));
                    
                    for(int y = 0; y <= yMax; y++){
                        c.clone().add(0, y, 0).getBlock().setType(Material.GRASS); // set wall
                    }
                }
            }
        }
        
        
        // Run path from point on edge to top of hill where slope is at most 1 (ideally 1/2)
    }
}
