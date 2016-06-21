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

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dallen.Kingdoms.Util.Annotations.SaveData;
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonKingdom;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonMunicipality;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonPlayerData;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonStructure;
import io.dallen.Kingdoms.Util.DBmanager;
import io.dallen.Kingdoms.Util.LogUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.reflections.Reflections;

/**
 *
 * @author Donovan Allen
 */
public class DataLoadHelper implements Listener{
    
    @SuppressWarnings("unchecked")
    public static boolean SaveKingdomData(){
        try {
            LogUtil.printDebug(Arrays.toString(Plot.getAllPlots().toArray()));
            for(Plot p : Plot.getAllPlots()){
                JsonStructure json = null;
                Class<? extends Plot> typ = null;
                if(p instanceof WallSystem.Wall){
                    typ = WallSystem.Wall.class;
                    WallSystem.Wall w = (WallSystem.Wall) p;
                    json = w.toJsonObject();
                    json.setType(WallSystem.Wall.class.getName());
                }
                if(json == null){
                    for(Class c : Main.getStructureClasses()){
                        if(p.getClass().isAssignableFrom(c)){
                            typ = c;
                            SaveType.Saveable sts = (SaveType.Saveable) p;
                            json = (JsonStructure) sts.toJsonObject();
                            json.setType(c.getName());
                        }
                    }
                }
                if(json == null){
                    typ = Plot.class;
                    json = p.toJsonObject();
                    json.setType(Plot.class.getName());
                }
                LogUtil.printDebug(Arrays.toString(typ.getDeclaredFields()));
                for(Field f : typ.getDeclaredFields()){
                    LogUtil.printDebug("Found field " + f.getName() + " of " + f.getType().getName());
                    if(!f.isAccessible()){
                        f.setAccessible(true);
                    }
                    if(f.isAnnotationPresent(SaveData.class)){
                        if(SaveType.Saveable.class.isAssignableFrom(f.getType())){
                            SaveType.NativeType ntv = (SaveType.NativeType) f.getType().getMethod("toJsonObject").invoke(f.get(p));
                            json.getAttr().put(f.getName(), new SaveType.SaveAttr(ntv.getClass(), ntv));
                        }else{
                            boolean found = false;
                            for(Class c : Main.getNativeTypes()){
                                for(Constructor ctr : c.getDeclaredConstructors()){
                                    if(Arrays.asList(ctr.getParameterTypes()).contains(f.getType())){
                                        json.getAttr().put(f.getName(), new SaveType.SaveAttr(c, ctr.newInstance(f.get(p))));
                                        found = true;
                                    }
                                }
                            }
                            if(!found)
                                json.getAttr().put(f.getName(), f.get(p));
                        }
                    }
                }
                LogUtil.printDebug(DBmanager.getJSonParser().writeValueAsString(json));
                DBmanager.saveObj(json, new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots"), 
                                    json.getStructureID() + ".plotdata");
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
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | JsonProcessingException ex) {
            Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
    public static boolean LoadKingdomData(){
        int MaxStructureID = 0;
        HashMap<String, Object> PlotObjs = DBmanager.loadAllObj(JsonStructure.class, new File(Main.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots"));
        for(Object o : PlotObjs.values()){
            try {
                JsonStructure js = (JsonStructure) o;
                Class type = Class.forName(js.getType());
                Plot p = (Plot) js.toJavaObject();
                for(Entry<String, Object> e : js.getAttr().entrySet()){
                    Object obj = e.getValue();
                    LogUtil.printDebug("obj type: " + e.getValue().getClass().getName());
                    LogUtil.printDebug("obj data: " + DBmanager.getJSonParser().writeValueAsString(e.getValue()));
                    if(e.getValue() instanceof LinkedHashMap && ((LinkedHashMap) e.getValue()).containsKey("type") && ((LinkedHashMap) e.getValue()).containsKey("data")){
                        SaveType.SaveAttr sv = DBmanager.getJSonParser().readValue(DBmanager.getJSonParser().writeValueAsString(e.getValue()), SaveType.SaveAttr.class);
                        obj = DBmanager.getJSonParser().readValue(DBmanager.getJSonParser().writeValueAsString(sv.getData()), sv.getType());
                        LogUtil.printDebug("obj type: " + obj.getClass().getName());
                        LogUtil.printDebug("obj data: " + DBmanager.getJSonParser().writeValueAsString(obj));
                    }
                    if(obj instanceof SaveType.NativeType && obj != null){
                        LogUtil.printDebug("Save Type");
                        obj = ((SaveType.NativeType) obj).toJavaObject();
                    }
                    if(obj instanceof BuildingVault){
                        LogUtil.printDebug("Building Vault");
                        ((BuildingVault) obj).setOwner(p);
                    }
                    Field dat = type.getDeclaredField(e.getKey());
                    if(!dat.isAccessible())
                        dat.setAccessible(true);
                    dat.set(p, obj);
                }
                if(MaxStructureID < js.getStructureID()){
                    MaxStructureID++;
                }
                Plot.getAllPlots().add(p);
            } catch (ClassNotFoundException | SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
                Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JsonProcessingException ex) {
                Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Structure.setCurrentID(MaxStructureID + 1);
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
