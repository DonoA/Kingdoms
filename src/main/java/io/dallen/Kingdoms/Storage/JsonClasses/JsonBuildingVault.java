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

import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonLocation;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonItemStack;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonNatives.JsonPolygon;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import io.dallen.Kingdoms.Kingdom.Vaults.PlayerVault;
import io.dallen.Kingdoms.Storage.MaterialWrapper;
import io.dallen.Kingdoms.Storage.SaveType;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonBuildingVault implements SaveType.NativeType.JsonType{
    @Getter @Setter
    private int owner;
    
    @Getter @Setter
    private double uniqueSize;
    
    @Getter @Setter
    private int fullSlots;
    
    @Getter @Setter
    private int capacity;
    
    @Getter @Setter
    private int amountFull;
    
    @Getter @Setter
    private JsonPolygon floorPlan;
    
    @Getter @Setter
    private JsonItemStack[] contents;
    
    @Getter
    private JsonResourcePile pile;
    
    public static class JsonResourcePile implements SaveType.NativeType.JsonType{
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
        BuildingVault bv = new BuildingVault((int) uniqueSize, capacity);
        bv.setFullSlots(fullSlots);
        bv.setAmountFull(amountFull);
        if(floorPlan != null){
            bv.setFloorPlan(floorPlan.toJavaObject());
        }else{
            bv.setFloorPlan(null);
        }
        ArrayList<MaterialWrapper> content = new ArrayList<MaterialWrapper>();
        for(JsonItemStack jis : contents){
            if(jis != null){
                content.add(new MaterialWrapper(jis.toJavaObject()));
            }else{
                content.add(null);
            }
        }
        bv.setContents(content.toArray(new ItemStack[] {}));
        return bv;
    }
    
}
