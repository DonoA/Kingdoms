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

import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author donoa_000
 */
public class Municipality {//Village, Town
    
    @Getter
    private HashMap<Class, ArrayList<Structure>> Structures = new HashMap<Class, ArrayList<Structure>>();
    
    @Getter
    private Structure Center;
    
    @Getter
    private WallSystem walls;
    
    @Getter @Setter
    private Polygon Base;
    
    @Getter
    private MunicipalTypes type;
    
    public Municipality(Structure center){
        this.Center = center;
        this.walls = new WallSystem(this);
        this.type = MunicipalTypes.VILLAGE;
    }
    
    public static enum MunicipalTypes{
        VILLAGE, MANOR, TOWN, CITY, KEEP, CITIDEL
    }
    
}
