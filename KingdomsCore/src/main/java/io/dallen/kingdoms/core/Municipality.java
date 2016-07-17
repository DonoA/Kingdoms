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
package io.dallen.kingdoms.core;

import io.dallen.kingdoms.core.Structures.Types.TownHall;
import io.dallen.kingdoms.core.Structures.Types.WallSystem;
import io.dallen.kingdoms.core.Structures.Types.WallSystem.Wall;
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Storage.JsonMunicipality;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Storage;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.core.Structures.Vaults.BuildingVault;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonEllipse;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonPolygon;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import io.dallen.kingdoms.utilities.Utils.LocationUtil;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

/**
 *
 * @author donoa_000
 */
public class Municipality implements SaveType.Saveable {

    @Getter
    @Setter
    private static int currentID = 0;

    @Getter
    private int MunicipalID;

    @Getter
    private final static ArrayList<Municipality> allMunicipals = new ArrayList<Municipality>();

    @Setter
    @Getter
    private final HashMap<Class, ArrayList<Structure>> Structures = new HashMap<Class, ArrayList<Structure>>();

    @Setter
    @Getter
    private Structure Center;

    @Setter
    @Getter
    private WallSystem walls;

    @Getter
    @Setter
    private Polygon Base;

    @Getter
    @Setter
    private Ellipse2D Influence;

    @Setter
    @Getter
    private MunicipalType type;

    @Setter
    @Getter
    private Kingdom Kingdom;

    @Setter
    @Getter
    private Date creation;

    @Getter
    @Setter
    private Location influenceCenter;

    public Municipality(Structure center) {
        this.Center = center;
        this.walls = new WallSystem(this);
        this.type = MunicipalType.MANOR;
        this.creation = new Date(System.currentTimeMillis());
        for (Class c : KingdomsCore.getStructureClasses()) {
            Structures.put(c, new ArrayList<Structure>());
        }
        Structures.put(Plot.class, new ArrayList<Structure>());
        Structures.get(TownHall.class).add(center);
        this.MunicipalID = currentID;
        currentID++;
        allMunicipals.add(this);
    }
    
    public Municipality() {
        for (Class c : KingdomsCore.getStructureClasses()) {
            Structures.put(c, new ArrayList<Structure>());
        }
        Structures.put(Plot.class, new ArrayList<Structure>());
    }

    public void createKingdom() {
        Kingdom = new Kingdom();
    }

    public static enum MunicipalType {

        // DAEN
        VILLAGE(100),
        MANOR(150),
        TOWN(200),
        CITY(250),
        KEEP(300),
        CITIDEL(400);

        @Getter
        private int radius;

        MunicipalType(int radius) {
            this.radius = radius;
        }
    }

    public static Municipality inMunicipal(Location l) {
        Municipality curr = null;
        Date oldDate = new Date(System.currentTimeMillis());
        for (Municipality m : allMunicipals) {
            if (m.getBase().contains(LocationUtil.asPoint(l))) {
                if (m.getCreation().before(oldDate)) {
                    oldDate = m.getCreation();
                    curr = m;
                }
            }
        }
        return curr;
    }

    public void addStructure(Structure s) {
        if (s instanceof Wall) {
            Wall w = (Wall) s;
            walls.getParts().get(w.getType()).add(w);
            w.setMunicipal(this);
        }
        for (Class c : KingdomsCore.getStructureClasses()) {
            if (s.getClass().isAssignableFrom(c)) {
                Structures.get(c).add(s);
                s.setMunicipal(this);
                return;
            }
        }
        if (s instanceof Plot) {
            Structures.get(Plot.class).add(s);
        }
    }

    public List<Structure> materialLocation(ItemStack is) {
        List<Structure> materialLocations = new ArrayList<Structure>();
        for (Class<? extends Plot> cls : KingdomsCore.getStructureClasses()) {
            if (Storage.class.isAssignableFrom(cls)) {
                for (Structure struct : this.Structures.get(cls)) {
                    Storage s = (Storage) struct;
                    if (((BuildingVault) s.getStorage()).getMaterial(is) != null) {
                        materialLocations.add(struct);
                    }
                }
            }
        }
        return materialLocations;
    }

    public void removeMaterial(ItemStack is) {
        for (Class<? extends Plot> cls : KingdomsCore.getStructureClasses()) {
            if (Storage.class.isAssignableFrom(cls)) {
                for (Structure struct : this.Structures.get(cls)) {
                    Storage s = (Storage) struct;
                    if (((BuildingVault) s.getStorage()).getMaterial(is) != null) {
                        is.setAmount(((BuildingVault) s.getStorage()).removeItem(is));
                        if (is.getAmount() == 0) {
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public JsonMunicipality toJsonObject() {
        JsonMunicipality jm = new JsonMunicipality();
        if (Base != null) {
            jm.setBase(new JsonPolygon(Base));
        } else {
            jm.setBase(null);
        }
        jm.setStructures(new ArrayList<Integer>());
        for (ArrayList<Structure> st : Structures.values()) {
            for (Structure s : st) {
                jm.getStructures().add(s.getStructureID());
            }
        }
        jm.setCenter(Center.getStructureID());
        jm.setCreation(creation);
        jm.setInfluence(new JsonEllipse(Influence));
        if (Kingdom != null) {
            jm.setKingdom(Kingdom.getKingdomID());
        } else {
            jm.setKingdom(-1);
        }
        jm.setMunicipalID(MunicipalID);
        jm.setType(type.name());
        jm.setWalls((walls != null ? walls.toJsonObject() : null));
        return jm;
    }
}
