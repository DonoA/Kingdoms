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
import io.dallen.Kingdoms.Storage.JsonClasses.JsonMunicipality;
import io.dallen.Kingdoms.Storage.SaveTypes;
import io.dallen.Kingdoms.Util.LocationUtil;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 *
 * @author donoa_000
 */
public class Municipality implements SaveTypes.Saveable{
    
    private static int currID;
    
    @Getter
    private int MunicipalID;
    
    @Getter
    private static ArrayList<Municipality> allMunicipals = new ArrayList<Municipality>();
    
    @Getter
    private HashMap<Class, ArrayList<Structure>> Structures = new HashMap<Class, ArrayList<Structure>>();
    
    @Getter
    private Structure Center;
    
    @Getter
    private WallSystem walls;
    
    @Getter @Setter
    private Polygon Base;
    
    @Getter @Setter
    private Ellipse2D Influence;
    
    @Getter
    private MunicipalTypes type;
    
    @Getter
    private Kingdom kingdom;
    
    @Getter
    private Date creation;
    
    public Municipality(Structure center){
        this.Center = center;
        this.walls = new WallSystem(this);
        this.type = MunicipalTypes.MANOR;
        this.creation = new Date(System.currentTimeMillis());
        Structures.put(Armory.class, new ArrayList<Structure>());
        Structures.put(Bank.class, new ArrayList<Structure>());
        Structures.put(Barracks.class, new ArrayList<Structure>());
        Structures.put(Blacksmith.class, new ArrayList<Structure>());
        Structures.put(BuildersHut.class, new ArrayList<Structure>());
        Structures.put(Castle.class, new ArrayList<Structure>());
        Structures.put(Dungeon.class, new ArrayList<Structure>());
        Structures.put(Farm.class, new ArrayList<Structure>());
        Structures.put(Marketplace.class, new ArrayList<Structure>());
        Structures.put(Stable.class, new ArrayList<Structure>());
        Structures.put(Storeroom.class, new ArrayList<Structure>());
        Structures.put(TownHall.class, new ArrayList<Structure>(Arrays.asList(new Structure[] {center})));
        Structures.put(TrainingGround.class, new ArrayList<Structure>());
    }
    
    public void createKingdom(){
        kingdom = new Kingdom();
    }
    
    public static enum MunicipalTypes{
        VILLAGE, MANOR, TOWN, CITY, KEEP, CITIDEL
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
    
    public JsonMunicipality toJsonObject(){
        throw new UnsupportedOperationException();
    }
}
