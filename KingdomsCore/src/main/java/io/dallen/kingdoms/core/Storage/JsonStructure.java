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
package io.dallen.kingdoms.core.Storage;

import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonLocation;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonPolygon;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonStructure implements SaveType.NativeType.JsonType{
    
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
    private int StructureID;
    @Getter @Setter
    private String Type;
    
    @Getter @Setter
    private HashMap<String, Object> attr = new HashMap<String, Object>();
    
    @Override
    public Structure toJavaObject(){
        try {
            Plot p = new Plot();
            p.setLength(Length);
            p.setWidth(Width);
            p.setHeight(Height);
            if(Base != null){
                p.setBase(Base.toJavaObject());
            }else{
                p.setBase(null);
            }
            p.setCenter(Center.toJavaObject());
            p.setOwner(Bukkit.getOfflinePlayer(Owner));
            p.setStructureID(StructureID);
            Class structure = Class.forName(Type);
            Constructor constructor = structure.getConstructor(new Class[] {Plot.class});
            Plot newPlot = (Plot) constructor.newInstance(p);
            return newPlot;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
            Logger.getLogger(JsonStructure.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
