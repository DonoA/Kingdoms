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
import io.dallen.Kingdoms.Storage.JsonClasses.JsonKingdom;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonPlayerData;
import io.dallen.Kingdoms.Storage.SaveTypes;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class Kingdom implements SaveTypes.Saveable{
    
    private static int currID;
    
    private String Leader;
    
    @Getter
    private int KingdomID;
    
    @Getter
    private Structure CapitolID;
    
    @Getter
    private String Name;
    
    @Getter
    private ArrayList<Ellipse2D> Base;
    
    private ResourceStats Resources;
    
    @Getter
    private ArrayList<Municipality> Municipals = new ArrayList<Municipality>();
    
    @Getter
    private HashMap<Class, ArrayList<Structure>> Structures = new HashMap<Class, ArrayList<Structure>>();
    
    @Getter
    private ArrayList<Player> OnlinePlayers = new ArrayList<Player>();
    
    @Override
    public JsonKingdom toJsonObject(){
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
