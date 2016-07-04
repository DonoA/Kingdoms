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
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.RPG.PlayerData;
import io.dallen.Kingdoms.Storage.SaveType;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonKingdom implements SaveType.NativeType.JsonType{
    
    @Getter @Setter
    private String Leader;
    
    @Getter @Setter
    private int CapitolID;
    
    @Getter @Setter
    private String Name;
    
    @Getter @Setter
    private ArrayList<JsonEllipse> Base;
    
    @Getter @Setter
    private ResourceStats Resources;
    
    @Getter @Setter
    private ArrayList<String> Municipals;
    
    @Override
    public Kingdom toJavaObject(){
        throw new UnsupportedOperationException();
    }
    
    @NoArgsConstructor
    private static class ResourceStats{
        @Getter @Setter
        private int Wealth;
        @Getter @Setter
        private int Grain;
        @Getter @Setter
        private int Sand;
        @Getter @Setter
        private int Wood;
        @Getter @Setter
        private int Ores;
        @Getter @Setter
        private int Stone;
        @Getter @Setter
        private int RefinedWood;
        @Getter @Setter
        private int Brick;
        @Getter @Setter
        private int Metal;
        @Getter @Setter
        private int Corps;
        @Getter @Setter
        private int Glass;
        
        @Getter @Setter
        private int StorageSpace;
        @Getter @Setter
        private int Population;
        
    }
}
