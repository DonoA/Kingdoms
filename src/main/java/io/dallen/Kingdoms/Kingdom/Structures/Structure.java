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
package io.dallen.Kingdoms.Kingdom.Structures;

import com.google.common.primitives.Ints;
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Util.ChestGUI;
import java.awt.Point;
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
    @Getter @Setter
    private int Area;
    @Getter
    private ChestGUI EditPlot;
    @Getter
    private long amountBuilt;
    
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
