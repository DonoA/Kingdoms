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

import io.dallen.Kingdoms.Util.Annotations.SaveData;
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonKingdom;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonMunicipality;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonPlayerData;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonStructure;
import io.dallen.Kingdoms.Util.DBmanager;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

/**
 *
 * @author Donovan Allen
 */
public class DataLoadHelper {
    
    @Getter
    private final static Class[] NativeTypes;
    
    static {
        Reflections reflections = new Reflections("io.dallen.Kingdoms.Storage");
        Set<Class<? extends SaveType.NativeType>> cs = reflections.getSubTypesOf(SaveType.NativeType.class);
        NativeTypes = (Class[]) cs.toArray();
    }
    
    @SuppressWarnings("unchecked")
    public static boolean SaveKingdomData(){
        try {
            int StructID = 0;
            for(Plot p : Plot.getAllPlots()){
                JsonStructure json = null;
                if(p instanceof WallSystem.Wall){
                    WallSystem.Wall w = (WallSystem.Wall) p;
                    json = w.toJsonObject();
                    json.setType(WallSystem.Wall.class.getName());
                    json.setStructureID(StructID);
                }
                if(json == null){
                    for(Class c : Municipality.getStructureClasses()){
                        if(p.getClass().isAssignableFrom(c)){
                            json = (JsonStructure) c.getMethod("toJsonObject").invoke(c.cast(p));
                            json.setType(c.getName());
                            json.setStructureID(StructID);
                        }
                    }
                }
                if(json == null){
                    json = p.toJsonObject();
                    json.setType(Plot.class.getName());
                    json.setStructureID(StructID);
                }
                for(Field f : p.getClass().getFields()){
                    if(f.isAnnotationPresent(SaveData.class)){
                        if(SaveType.Saveable.class.isAssignableFrom(f.getType())){
                            SaveType.NativeType ntv = (SaveType.NativeType) f.getType().getMethod("toJsonObject").invoke(f.get(p));
                            json.getAttr().put(f.getName(), ntv);
                        }else{
                            json.getAttr().put(f.getName(), f.get(p));
                        }
                    }
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
    
    @SuppressWarnings("unchecked")
    public static boolean LoadKingdomData(){
        HashMap<String, Object> PlotObjs = DBmanager.loadAllObj(Plot.class, new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots"));
        for(Object o : PlotObjs.values()){
            try {
                JsonStructure js = (JsonStructure) o;
                Class type = Class.forName(js.getType());
                Plot p = (Plot) type.cast(js.toJavaObject());
                for(Entry<String, Object> e : js.getAttr().entrySet()){
                    Object obj = e.getValue();
                    if(SaveType.class.isAssignableFrom(e.getValue().getClass())){
                        obj = e.getValue().getClass().getMethod("toJavaObject").invoke(SaveType.NativeType.class.cast(e.getValue()));
                    }
                    type.getMethod("set"+capitalize(e.getKey()), Object.class).invoke(p, obj);
                }
                Plot.getAllPlots().add(p);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
    
    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
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
