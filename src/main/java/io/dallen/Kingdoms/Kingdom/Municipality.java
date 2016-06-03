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

import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Armory;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Bank;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Barracks;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Blacksmith;
import io.dallen.Kingdoms.Kingdom.Structures.Types.BuildersHut;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Castle;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Dungeon;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Farm;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Marketplace;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Stable;
import io.dallen.Kingdoms.Kingdom.Structures.Types.Storeroom;
import io.dallen.Kingdoms.Kingdom.Structures.Types.TownHall;
import io.dallen.Kingdoms.Kingdom.Structures.Types.TrainingGround;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.Wall;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonMunicipality;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonEllipse;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonLocation;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonPolygon;
import io.dallen.Kingdoms.Storage.SaveTypes;
import io.dallen.Kingdoms.Util.LocationUtil;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 *
 * @author donoa_000
 */
public class Municipality implements SaveTypes.Saveable{
    
    @Getter
    private int MunicipalID;
    
    @Getter
    private final static ArrayList<Municipality> allMunicipals = new ArrayList<Municipality>();
    
    @Getter
    private final HashMap<Class, ArrayList<Structure>> Structures = new HashMap<Class, ArrayList<Structure>>();
    
    @Getter
    private Structure Center;
    
    @Getter
    private WallSystem walls;
    
    @Getter @Setter
    private Polygon Base;
    
    @Getter @Setter
    private Ellipse2D Influence;
    
    @Getter
    private MunicipalType type;
    
    @Getter
    private Kingdom Kingdom;
    
    @Getter
    private Date creation;
    
    @Getter @Setter
    private Location influenceCenter;
    
    @Getter
    private final static Class[] StructureClasses = new Class[] {Armory.class, Bank.class, Barracks.class, Blacksmith.class, 
                                                                BuildersHut.class, Castle.class, Dungeon.class, Farm.class, 
                                                                Marketplace.class, Stable.class, Storeroom.class, TownHall.class, 
                                                                TrainingGround.class};
    
    public Municipality(Structure center){
        this.Center = center;
        this.walls = new WallSystem(this);
        this.type = MunicipalType.MANOR;
        this.creation = new Date(System.currentTimeMillis());
        for(Class c : StructureClasses){
            Structures.put(c, new ArrayList<Structure>());
        }
        Structures.put(Plot.class, new ArrayList<Structure>());
        Structures.get(TownHall.class).add(center);
        this.MunicipalID = allMunicipals.size();
        allMunicipals.add(this);
    }
    
    public void createKingdom(){
        Kingdom = new Kingdom();
    }
    
    public static enum MunicipalType{
        // DAEN
        VILLAGE(100), MANOR(150), TOWN(200), CITY(250), KEEP(300), CITIDEL(400);
        
        @Getter
        private int radius;
        
        MunicipalType(int radius){
            this.radius = radius;
        }
    }
    
    public static Municipality inMunicipal(Location l){
        Municipality curr = null;
        Date oldDate = new Date(System.currentTimeMillis());
        for(Municipality m : allMunicipals){
            if(m.getBase().contains(LocationUtil.asPoint(l))){
                if(m.getCreation().before(oldDate)){
                    oldDate = m.getCreation();
                    curr = m;
                }
            }
        }
        return curr;
    }
    
    public void addStructure(Structure s){
        if(s instanceof Wall){
            Wall w = (Wall) s;
            walls.getParts().get(w.getType()).add(w);
            w.setMunicipal(this);
        }
        for(Class c : StructureClasses){
            if(s.getClass().isAssignableFrom(c)){
                Structures.get(c).add(s);
                s.setMunicipal(this);
                return;
            }
        }
        if(s instanceof Plot){
            Structures.get(Plot.class).add(s);
        }
    }
    
    @Override
    public JsonMunicipality toJsonObject(){
        JsonMunicipality jm = new JsonMunicipality();
        jm.setBase(new JsonPolygon(Base));
        jm.setCenter(Center.getStructureID());
        jm.setCreation(creation);
        jm.setInfluence(new JsonEllipse(Influence));
        if(Kingdom != null)
            jm.setKingdom(Kingdom.getKingdomID());
        else
            jm.setKingdom(-1);
        jm.setMunicipalID(MunicipalID);
        
        jm.setType(type);
        jm.setWalls(walls.toJsonObject());
        return jm;
    }
}
