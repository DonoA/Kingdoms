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

import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonLocation;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonPolygon;
import io.dallen.Kingdoms.Storage.SaveTypes;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonStructure implements SaveTypes.JsonType{
    
    @Getter @Setter
    private int Width; // X
    @Getter @Setter
    private int Height; // Y
    @Getter @Setter
    private int Length; // Z
    @Getter @Setter
    private JsonPolygon Base; // Z
    @Getter @Setter
    private JsonLocation Center;
    @Getter @Setter
    private UUID Owner;
    @Getter @Setter
    private int Kingdom;
    @Getter @Setter
    private int Municipal;
//    @Getter @Setter
//    private int Rank;
//    @Getter @Setter
//    private int maxRank;
    @Getter @Setter
    private String StructureType;
    @Getter @Setter
    private int StructureID;
    
    @Override
    public Structure toJavaObject(){
        throw new UnsupportedOperationException();
    }
    
}
