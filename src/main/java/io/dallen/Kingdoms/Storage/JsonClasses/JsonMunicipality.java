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
package io.dallen.Kingdoms.Storage.JsonClasses;

import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonEllipse;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonPolygon;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Municipality.MunicipalType;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Storage.SaveType;
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
public class JsonMunicipality implements SaveType.NativeType.JsonType{
    
    @Getter @Setter
    private HashMap<String, ArrayList<Structure>> Structures;
    
    @Getter @Setter
    private int Center;
    
    @Getter @Setter
    private int MunicipalID;
    
    @Getter @Setter
    private JsonWallSystem walls;
    
    @Getter @Setter
    private JsonPolygon Base;
    
    @Getter @Setter
    private JsonEllipse Influence;
    
    @Getter @Setter
    private MunicipalType type;
    
    @Getter @Setter
    private int kingdom;
    
    @Getter @Setter
    private Date creation;
    
    @Override
    public Municipality toJavaObject(){
        throw new UnsupportedOperationException();
    }
    
}
