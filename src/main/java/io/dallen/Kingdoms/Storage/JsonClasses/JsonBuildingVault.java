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

import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import io.dallen.Kingdoms.Kingdom.Vaults.PlayerVault;
import io.dallen.Kingdoms.Storage.SaveTypes;
import lombok.Getter;

/**
 *
 * @author Donovan Allen
 */
public class JsonBuildingVault implements SaveTypes.JsonType{
    @Getter
    private JsonStructure owner;
    
    @Getter
    private double uniqueSize;
    
    @Getter
    private int fullSlots;
    
    @Getter
    private int capacity;
    
    @Getter
    private int amountFull;
    
    @Getter
    private JsonPolygon floorPlan;
    
    @Getter
    private JsonItemStack[] contents;
    
    @Getter
    private JsonResourcePile pile;
    
    public static class JsonResourcePile implements SaveTypes.JsonType{
        @Getter
        private JsonLocation startLoc;
        @Getter
        private double dist = 0.5;
        @Getter
        private double angleA = 0;
        @Getter
        private double angleB = 0;
        
        @Override
        public BuildingVault.ResourcePile toJavaObject(){
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public BuildingVault toJavaObject(){
        throw new UnsupportedOperationException();
    }
    
}
