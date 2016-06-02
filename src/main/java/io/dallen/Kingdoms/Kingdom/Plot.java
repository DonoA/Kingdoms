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

import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Kingdom.Structures.Contract;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.Wall;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.WallType;
import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Util.LocationUtil;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author donoa_000
 */
public class Plot extends Structure implements Listener{
    
    @Getter
    private static ArrayList<Plot> allPlots = new ArrayList<Plot>();
    
    @Getter
    private static ArrayList<Plot> nonMunicipalPlots = new ArrayList<Plot>();//TODO make this var used
    
    @Getter
    private ArrayList<Contract> contracts = new ArrayList<Contract>();
    
    @Getter @Setter
    private boolean empty = true;
    
    public Plot(Polygon base, Location cent, OfflinePlayer own, Municipality mun) {
        super(base, cent, own, mun);
    }
    
    public Plot(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getKingdom(), p.getMunicipal());
    }
    
    public static Plot inPlot(Location l){
        for(Plot p : allPlots){
            if(p.getBase().contains(new Point(l.getBlockX(),l.getBlockZ())) || 
                   (p.getBase().contains(new Point(l.getBlockX()-1,l.getBlockZ())) || 
                    p.getBase().contains(new Point(l.getBlockX(),l.getBlockZ()-1)) || 
                    p.getBase().contains(new Point(l.getBlockX()-1,l.getBlockZ()-1)))){
                return p;
            }
        }
        return null;
    }
    
    public boolean contains(Location l){
        return super.getBase().contains(new Point(l.getBlockX(),l.getBlockZ())) || 
              (super.getBase().contains(new Point(l.getBlockX()-1,l.getBlockZ())) || 
               super.getBase().contains(new Point(l.getBlockX(),l.getBlockZ()-1)) || 
               super.getBase().contains(new Point(l.getBlockX()-1,l.getBlockZ()-1)));
    }
    
    public boolean isValid(){
        for(Plot p : allPlots){
            if(p.getBase().intersects(super.getBase().getBounds())){
                return false;
            }
        }
        return true;
    }
    
    public boolean createMucicpal(){
        if(super.getMunicipal() != null){
            return false;
        }
        super.setMunicipal(new Municipality((Structure) this));
        return true;
    }
    
    public boolean canBuild(Player p){
        return super.getOwner().equals(p);
    }
    
}
