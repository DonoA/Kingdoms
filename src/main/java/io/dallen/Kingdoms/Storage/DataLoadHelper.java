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

import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonKingdom;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonMunicipality;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonPlayerData;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonStructure;
import io.dallen.Kingdoms.Util.DBmanager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 *
 * @author Donovan Allen
 */
public class DataLoadHelper {
    
    @SuppressWarnings("unchecked")
    public static boolean SaveKingdomData(){
        try {
            boolean cast = false;
            int StructID = 0;
            for(Plot p : Plot.getAllPlots()){
                cast = false;
                JsonStructure json = null;
                if(p instanceof WallSystem.Wall){
                    WallSystem.Wall w = (WallSystem.Wall) p;
                    json = w.toJsonObject();
                    json.setType(WallSystem.Wall.class.getName());
                    json.setStructureID(StructID);
                    cast = true;
                }
                if(!cast){
                    for(Class c : Municipality.getStructureClasses()){
                        if(p.getClass().isAssignableFrom(c)){
                            json = (JsonStructure) c.cast(p).getClass().getMethod("toJsonObject").invoke(c);
                            json.setType(c.getName());
                            json.setStructureID(StructID);
                            cast = true;
                        }
                    }
                }
                if(p instanceof Plot && !cast){
                    json = p.toJsonObject();
                    json.setType(Plot.class.getName());
                    json.setStructureID(StructID);
                }
                DBmanager.saveObj(json, new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots"), 
                                    StructID + ".plotdata");
                StructID++;
            }
            for(Municipality m : Municipality.getAllMunicipals()){
                JsonMunicipality json = m.toJsonObject();
                DBmanager.saveObj(json, new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "municipals"), 
                                    m.getMunicipalID() + ".municipaldata");
            }
            for(Kingdom k : Kingdom.getAllKingdoms()){
                JsonKingdom json = k.toJsonObject();
                DBmanager.saveObj(json, new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "kingdoms"), 
                                    k.getKingdomID() + ".kingdomdata");
            }
            return true;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static boolean LoadKingdomData(){
        throw new UnsupportedOperationException();
    }
    
    public static boolean SavePlayerData(PlayerData pd){
        JsonPlayerData jpd = pd.toJsonObject();
        return DBmanager.saveObj(jpd, new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "playerdata"), 
                pd.getPlayer().getUniqueId().toString() + ".playerdata");
    }
    
    public PlayerData LoadPlayerData(Player p){
        Object jpd = DBmanager.loadObj(JsonPlayerData.class, new File(Main.getPlugin().getDataFolder() + 
                DBmanager.getFileSep() + "playerdata" + DBmanager.getFileSep() + p.getUniqueId().toString() + ".playerdata"));
        if(jpd instanceof JsonPlayerData){
            PlayerData pd = ((JsonPlayerData) jpd).toJavaObject();//TODO make this method have content
            pd.setPlayer(p);
            return pd;
        }else{
            return new PlayerData(p);
        }
    }
}
