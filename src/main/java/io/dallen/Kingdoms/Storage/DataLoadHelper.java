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
package io.dallen.Kingdoms.Storage;

import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonStructure;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Donovan Allen
 */
public class DataLoadHelper {
    
    @SuppressWarnings("unchecked")
    public boolean SaveKingdomData(){
        try {
            boolean cast = false;
            int StructID = 0;
            for(Plot p : Plot.getAllPlots()){
                cast = false;
                if(p instanceof WallSystem.Wall){
                    WallSystem.Wall w = (WallSystem.Wall) p;
                    JsonStructure json = w.toJsonObject();
                    json.setType(WallSystem.Wall.class.getName());
                    json.setStructureID(StructID);
                    
                    cast = true;
                }
                if(!cast){
                    for(Class c : Municipality.getStructureClasses()){
                        if(p.getClass().isAssignableFrom(c)){
                            Object json = c.cast(p).getClass().getMethod("toJsonObject").invoke(c);
                            json.getClass().getMethod("setType", String.class).invoke(json, c.getName());
                            json.getClass().getMethod("setStructureID", int.class).invoke(json, StructID);
                            cast = true;
                        }
                    }
                }
                if(p instanceof Plot && !cast){
                    JsonStructure json = p.toJsonObject();
                    json.setType(Plot.class.getName());
                    json.setStructureID(StructID);
                }
                StructID++;
            }
            return true;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
