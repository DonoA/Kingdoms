/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of Kingdoms for the Morphics Network.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 */
package io.dallen.Kingdoms.Util.JsonClasses;

import io.dallen.Kingdoms.Kingdom.Municipality.MunicipalTypes;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonMunicipality {
    
    @Getter @Setter
    private HashMap<String, ArrayList<Structure>> Structures;
    
    @Getter @Setter
    private JsonStructure Center;
    
    @Getter @Setter
    private JsonWallSystem walls;
    
    @Getter @Setter
    private JsonPolygon Base;
    
    @Getter @Setter
    private JsonEllipse Influence;
    
    @Getter @Setter
    private MunicipalTypes type;
    
    @Getter @Setter
    private String kingdom;
    
    @Getter @Setter
    private Date creation;
    
}
