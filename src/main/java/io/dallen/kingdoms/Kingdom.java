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

import io.dallen.kingdoms.ThreadHandler.BuilderThread;
import io.dallen.utils.LogUtil;
import io.dallen.utils.Math.Vector3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;


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
        double GateSize = 8;
        // (X-h)^2/a^2 + (Y-k)^2/b^2 = 1
        // Precompute squares
        double A2 = A*A;
        double B2 = B*B;
        // Get top left and bottom right corners
        Location c1 = center.clone();
        c1.add(-A, 0, -B);
        Location c2 = center.clone();
        c2.add(A, 0, B);
        // Pick gate location
        String dir = (new String[] {"North", "South"})[(int)Math.floor(2.0*Math.random())];
        // Locate the maxima of the hill
        Location palaceFront = new Location(null, 0, 0, 0);
        Location gateCenter = null;
        // scan rectangle
        for(int x = c1.getBlockX()-1; x <= c2.getBlockX()+1; x++){
            double zScale = 0;
            for(int z = c1.getBlockZ()-1; z <= c2.getBlockZ()+1; z++){
                Location c = new Location(center.getWorld(), x, center.getBlockY(), z);
                BuilderThread.add(c, Material.STONE); // show scanned area
                double func = (Math.pow(x-center.getBlockX(), 2)/A2)+(Math.pow(z-center.getBlockZ(), 2)/B2);
                
                if(Math.abs(func - 1.0) < 0.05){ // generate wall
                    zScale = Math.PI/(2.0*Math.sqrt(c.distanceSquared(center)-Math.pow(x-center.getBlockX(), 2)));
                    if(Math.abs(x-center.getBlockX())<GateSize &&
                            ((z-center.getBlockZ()>0 && dir.equals("North"))||
                             (z-center.getBlockZ()<0 && dir.equals("South"))
                            )){
                        if(x-center.getBlockX() == 0){
                            gateCenter = c.clone();
                        }
                        for(int y = 0; y < 7; y++){
                            BuilderThread.add(c.clone().add(0, y, 0), Material.WOOD); // set gate
                        }
                    }else{
                        for(int y = 0; y < 7; y++){
                            BuilderThread.add(c.clone().add(0, y, 0), Material.COBBLESTONE); // set wall
                        }
                    }
                }else if(func < 1.0){ // interior
                    // Fill it with hill that is sharp on one side
                    
                    // 1/2*sin(Ax/3)*(Ax+3sin(Ax)/2) in x direction 0 - 3pi domain should cover longest possible x len, scale with A
                    // 2/3*sin(Ax) in z direction 0 - pi domain should cover hight of ellipse scale with A
                    
                    double xScale = (3*Math.PI)/(2*A);
                    double absX = xScale*(x - c1.getBlockX());
                    
                    double absZ = zScale*(Math.abs(z - c1.getBlockZ())-B);
                    
                    int yMax = (int) Math.floor(HeightScale*(0.5*Math.sin(absX/3.0)*(absX+1.5*Math.sin(absX)))*((2.0/3.0)*Math.cos(absZ)));
                    
                    if(yMax > palaceFront.getBlockY()){
                        palaceFront = c.clone().add(0, yMax, 0);
                    }
                    
                    for(int y = 0; y <= yMax; y++){
                        BuilderThread.add(c.clone().add(0, y, 0), y==yMax?Material.GRASS:Material.DIRT); // set ground
                    }
                }
            }
        }
        // Run path from point on edge to top of hill where slope is at most 1 (ideally 1/2)
        Vector3D path = new Vector3D(palaceFront.getX() - gateCenter.getX(), 0, palaceFront.getZ() - gateCenter.getZ());
        LogUtil.printDebug(path.toString());
        Vector3D curve = Vector3D.cross(new Vector3D(0, (dir.equals("North")?1:-1), 0), path); // This generates a vector that will push the path in a sin curve
        curve.normalize();
        // This is scale factor we will need
        double distScale = Math.PI/(gateCenter.clone().add(0, palaceFront.getBlockY() + 5, 0).distance(palaceFront.clone().add(0, 5, 0))-1); 
        LogUtil.printDebug(palaceFront.toString());
        path.normalize(); // Ensure a nice packed path
        LogUtil.printDebug(path.toString());
        double t = 0;
        double oldDist = 0;
        Location loc = gateCenter.clone().add(0, palaceFront.getBlockY() + 5, 0).add(Vector3D.dot(t, path).toBukkitVector());
        do{
            t++;
            oldDist = loc.distance(palaceFront.clone().add(0, 5, 0));
            loc = gateCenter.clone().add(0, palaceFront.getBlockY() + 5, 0).add(Vector3D.dot(t, path).toBukkitVector());
            BuilderThread.add(loc.clone().add(Vector3D.dot(20.0*Math.sin(distScale*t), curve).toBukkitVector()), Material.REDSTONE_BLOCK);
            LogUtil.printDebug(oldDist);
            LogUtil.printDebug(loc.distance(palaceFront.clone().add(0, 5, 0)));
        }while(oldDist >= loc.distance(palaceFront.clone().add(0, 5, 0)));
    }
}
