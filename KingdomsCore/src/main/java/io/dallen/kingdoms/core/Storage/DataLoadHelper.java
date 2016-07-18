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

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dallen.kingdoms.core.Kingdom;
import io.dallen.kingdoms.core.KingdomModual;
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.core.Municipality;
import io.dallen.kingdoms.core.PlayerData;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Structure;
import io.dallen.kingdoms.core.Structures.Types.WallSystem;
import io.dallen.kingdoms.core.Structures.Vaults.BuildingVault;
import io.dallen.kingdoms.utilities.Annotations.SaveData;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import io.dallen.kingdoms.utilities.Storage.SaveType.NativeType;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import io.dallen.kingdoms.utilities.Utils.LogUtil;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 *
 * @author Donovan Allen
 */
public class DataLoadHelper implements Listener {

    @SuppressWarnings("unchecked")
    public static boolean SaveKingdomData() {
        try {
            for (Plot p : Plot.getAllPlots()) {
                JsonStructure json = null;
                Class<? extends Plot> typ = null;
                if (p instanceof WallSystem.Wall) {
                    typ = WallSystem.Wall.class;
                    WallSystem.Wall w = (WallSystem.Wall) p;
                    json = w.toJsonObject();
                    json.setType(WallSystem.Wall.class.getName());
                }
                if (json == null) {
                    for (Class c : KingdomsCore.getStructureClasses()) {
                        if (p.getClass().isAssignableFrom(c)) {
                            typ = c;
                            SaveType.Saveable sts = (SaveType.Saveable) p;
                            json = (JsonStructure) sts.toJsonObject();
                            json.setType(c.getName());
                        }
                    }
                }
                if (json == null) {
                    typ = Plot.class;
                    json = p.toJsonObject();
                    json.setType(Plot.class.getName());
                }
                for (Field f : typ.getDeclaredFields()) {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    if (f.isAnnotationPresent(SaveData.class)) {
                        if (SaveType.Saveable.class.isAssignableFrom(f.getType())) {
                            SaveType.NativeType ntv = (SaveType.NativeType) f.getType().getMethod("toJsonObject").invoke(f.get(p));
                            json.getAttr().put(f.getName(), new SaveType.SaveAttr(ntv.getClass(), ntv));
                        } else {
                            boolean found = false;
                            for (Class<? extends NativeType> c : KingdomsCore.getNativeTypes()) {
                                for (Constructor<?> ctr : c.getDeclaredConstructors()) {
                                    if (Arrays.asList(ctr.getParameterTypes()).contains(f.getType())) {
                                        json.getAttr().put(f.getName(), new SaveType.SaveAttr(c, ctr.newInstance(f.get(p))));
                                        found = true;
                                    }
                                }
                            }
                            if (!found) {
                                json.getAttr().put(f.getName(), f.get(p));
                            }
                        }
                    }
                }
                DBmanager.saveObj(json, new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots"),
                        json.getStructureID() + ".plotdata");
            }
            for (Municipality m : Municipality.getAllMunicipals()) {
                JsonMunicipality json = m.toJsonObject();
                DBmanager.saveObj(json, new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "municipals"),
                        m.getMunicipalID() + ".municipaldata");
            }
            for (Kingdom k : Kingdom.getAllKingdoms()) {
                JsonKingdom json = k.toJsonObject();
                DBmanager.saveObj(json, new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "kingdoms"),
                        k.getKingdomID() + ".kingdomdata");
            }
            return true;
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean LoadKingdomData() {
        int MaxStructureID = -1;
        int MaxMunicipalID = -1;
        int MaxKingdomID = -1;
        ArrayList<Integer> lonePlots = new ArrayList<>();
        ArrayList<Integer> loneMunicipals = new ArrayList<>();
        for (File f : new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep()
                + "municipals").listFiles()) {
            if (f.getName().contains(".municipaldata")) {
                loneMunicipals.add(Integer.parseInt(f.getName().replace(".municipaldata", "")));
            }
        }
        for (File f : new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep()
                + "plots").listFiles()) {
            if (f.getName().contains(".plotdata")) {
                lonePlots.add(Integer.parseInt(f.getName().replace(".plotdata", "")));
            }
        }
        HashMap<String, Object> KingdomObjs = DBmanager.loadAllObj(JsonKingdom.class, new File(KingdomsCore.getPlugin().getDataFolder()
                + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "kingdoms"));
        for (Object o : KingdomObjs.values()) {
            JsonKingdom jk = (JsonKingdom) o;
            Kingdom kingdom = jk.toJavaObject();
            for (Integer mid : jk.getMunicipals()) {
                loneMunicipals.remove(mid);
                JsonMunicipality jm = (JsonMunicipality) DBmanager.loadObj(JsonMunicipality.class, new File(KingdomsCore.getPlugin().getDataFolder()
                        + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "municipals" + DBmanager.getFileSep()
                        + mid + ".municipaldata"));
                Municipality muni = jm.toJavaObject();
                for (Integer sid : jm.getStructures()) {
                    lonePlots.remove(sid);
                    try {
                        JsonStructure js = (JsonStructure) DBmanager.loadObj(JsonStructure.class, new File(KingdomsCore.getPlugin().getDataFolder()
                                + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots" + DBmanager.getFileSep()
                                + sid + ".plotdata"));;
                        Class<?> type = Class.forName(js.getType());
                        Plot p = (Plot) js.toJavaObject();
                        for (Entry<String, Object> e : js.getAttr().entrySet()) {
                            Object obj = e.getValue();
                            if (e.getValue() instanceof LinkedHashMap && ((LinkedHashMap) e.getValue()).containsKey("type") && ((LinkedHashMap) e.getValue()).containsKey("data")) {
                                obj = DBmanager.getJSonParser().readValue(DBmanager.getJSonParser().writeValueAsString(((LinkedHashMap) e.getValue()).get("data")),
                                        Class.forName((String) ((LinkedHashMap) e.getValue()).get("type")));
                            }
                            if (obj != null && obj instanceof SaveType.NativeType) {
                                obj = ((SaveType.NativeType) obj).toJavaObject();
                            }
                            if (obj instanceof BuildingVault) {
                                ((BuildingVault) obj).setOwner(p);
                            }
                            Field dat = type.getDeclaredField(e.getKey());
                            if (!dat.isAccessible()) {
                                dat.setAccessible(true);
                            }
                            dat.set(p, obj);
                        }
                        if (MaxStructureID < js.getStructureID()) {
                            MaxStructureID++;
                        }
                        Plot.getAllPlots().add(p);
                        muni.addStructure(p);
                        p.setMunicipal(muni);
                        if (jm.getCenter() == sid) {
                            muni.setCenter(p);
                        }
                    } catch (ClassNotFoundException | SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
                        Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (JsonProcessingException ex) {
                        Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (muni.getBase() == null) {
                    muni.setBase(new Polygon(new int[]{
                        muni.getCenter().getCenter().getBlockX() - muni.getType().getRadius() / 8,
                        muni.getCenter().getCenter().getBlockX() - muni.getType().getRadius() / 8,
                        muni.getCenter().getCenter().getBlockX() + muni.getType().getRadius() / 8,
                        muni.getCenter().getCenter().getBlockX() + muni.getType().getRadius() / 8
                    }, new int[]{
                        muni.getCenter().getCenter().getBlockZ() - muni.getType().getRadius() / 8,
                        muni.getCenter().getCenter().getBlockZ() + muni.getType().getRadius() / 8,
                        muni.getCenter().getCenter().getBlockZ() + muni.getType().getRadius() / 8,
                        muni.getCenter().getCenter().getBlockZ() - muni.getType().getRadius() / 8
                    }, 4));
                }
                //TODO load rest of municipal data
                //TODO setup wall system with newly loaded plots
                Municipality.getAllMunicipals().add(muni);
                kingdom.getMunicipals().add(muni);
                if (MaxMunicipalID < jm.getMunicipalID()) {
                    MaxMunicipalID++;
                }
            }
            for (Integer sid : jk.getPlots()) {
                lonePlots.remove(sid);
                try {
                    JsonStructure js = (JsonStructure) DBmanager.loadObj(JsonStructure.class, new File(KingdomsCore.getPlugin().getDataFolder()
                            + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots" + DBmanager.getFileSep()
                            + sid + ".plotdata"));
                    Class<?> type = Class.forName(js.getType());
                    Plot p = (Plot) js.toJavaObject();
                    for (Entry<String, Object> e : js.getAttr().entrySet()) {
                        Object obj = e.getValue();
                        if (e.getValue() instanceof LinkedHashMap && ((LinkedHashMap) e.getValue()).containsKey("type") && ((LinkedHashMap) e.getValue()).containsKey("data")) {
                            obj = DBmanager.getJSonParser().readValue(DBmanager.getJSonParser().writeValueAsString(((LinkedHashMap) e.getValue()).get("data")),
                                    Class.forName((String) ((LinkedHashMap) e.getValue()).get("type")));
                        }
                        if (obj instanceof SaveType.NativeType && obj != null) {
                            obj = ((SaveType.NativeType) obj).toJavaObject();
                        }
                        if (obj instanceof BuildingVault) {
                            ((BuildingVault) obj).setOwner(p);
                        }
                        Field dat = type.getDeclaredField(e.getKey());
                        if (!dat.isAccessible()) {
                            dat.setAccessible(true);
                        }
                        dat.set(p, obj);
                    }
                    if (MaxStructureID < js.getStructureID()) {
                        MaxStructureID++;
                    }
                    Plot.getAllPlots().add(p);
                    kingdom.addStructure(p);
                    p.setKingdom(kingdom);
                } catch (ClassNotFoundException | SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
                    Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //TODO load rest of kingdom data
            Kingdom.getAllKingdoms().add(kingdom);
            if (MaxKingdomID < jk.getKingdomID()) {
                MaxKingdomID++;
            }
        }
        for (Integer mid : loneMunicipals) {
            JsonMunicipality jm = (JsonMunicipality) DBmanager.loadObj(JsonMunicipality.class, new File(KingdomsCore.getPlugin().getDataFolder()
                    + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "municipals" + DBmanager.getFileSep()
                    + mid + ".municipaldata"));
            Municipality muni = jm.toJavaObject();
            for (Integer sid : jm.getStructures()) {
                lonePlots.remove(sid);
                try {
                    JsonStructure js = (JsonStructure) DBmanager.loadObj(JsonStructure.class, new File(KingdomsCore.getPlugin().getDataFolder()
                            + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots" + DBmanager.getFileSep()
                            + sid + ".plotdata"));;
                    Class<?> type = Class.forName(js.getType());
                    Plot p = (Plot) js.toJavaObject();
                    for (Entry<String, Object> e : js.getAttr().entrySet()) {
                        Object obj = e.getValue();
                        if (e.getValue() instanceof LinkedHashMap && ((LinkedHashMap) e.getValue()).containsKey("type") && ((LinkedHashMap) e.getValue()).containsKey("data")) {
                            obj = DBmanager.getJSonParser().readValue(DBmanager.getJSonParser().writeValueAsString(((LinkedHashMap) e.getValue()).get("data")),
                                    Class.forName((String) ((LinkedHashMap) e.getValue()).get("type")));
                        }
                        if (obj != null && obj instanceof SaveType.NativeType) {
                            obj = ((SaveType.NativeType) obj).toJavaObject();
                        }
                        if (obj instanceof BuildingVault) {
                            ((BuildingVault) obj).setOwner(p);
                        }
                        Field dat = type.getDeclaredField(e.getKey());
                        if (!dat.isAccessible()) {
                            dat.setAccessible(true);
                        }
                        dat.set(p, obj);
                    }
                    if (MaxStructureID < js.getStructureID()) {
                        MaxStructureID++;
                    }
                    Plot.getAllPlots().add(p);
                    muni.addStructure(p);
                    p.setMunicipal(muni);
                    if (jm.getCenter() == sid) {
                        muni.setCenter(p);
                    }
                } catch (ClassNotFoundException | SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
                    Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JsonProcessingException ex) {
                    Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //TODO load rest of municipal data
            Municipality.getAllMunicipals().add(muni);
            if (MaxMunicipalID < jm.getMunicipalID()) {
                MaxMunicipalID++;
            }
        }
        for (Integer sid : lonePlots) {
            try {
                JsonStructure js = (JsonStructure) DBmanager.loadObj(JsonStructure.class, new File(KingdomsCore.getPlugin().getDataFolder()
                        + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + "plots" + DBmanager.getFileSep()
                        + sid + ".plotdata"));;
                Class<?> type = Class.forName(js.getType());
                Plot p = (Plot) js.toJavaObject();
                for (Entry<String, Object> e : js.getAttr().entrySet()) {
                    Object obj = e.getValue();
                    if (e.getValue() instanceof LinkedHashMap && ((LinkedHashMap) e.getValue()).containsKey("type") && ((LinkedHashMap) e.getValue()).containsKey("data")) {
                        obj = DBmanager.getJSonParser().readValue(DBmanager.getJSonParser().writeValueAsString(((LinkedHashMap) e.getValue()).get("data")),
                                Class.forName((String) ((LinkedHashMap) e.getValue()).get("type")));
                    }
                    if (obj instanceof SaveType.NativeType && obj != null) {
                        obj = ((SaveType.NativeType) obj).toJavaObject();
                    }
                    if (obj instanceof BuildingVault) {
                        ((BuildingVault) obj).setOwner(p);
                    }
                    Field dat = type.getDeclaredField(e.getKey());
                    if (!dat.isAccessible()) {
                        dat.setAccessible(true);
                    }
                    dat.set(p, obj);
                }
                if (MaxStructureID < js.getStructureID()) {
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
        Kingdom.setCurrentID(MaxKingdomID + 1);
        Municipality.setCurrentID(MaxMunicipalID + 1);
        Structure.setCurrentID(MaxStructureID + 1);
        return true;
    }

    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static boolean SavePlayerData(PlayerData pd) {
        JsonPlayerData jpd = pd.toJsonObject();
        for (KingdomModual km : KingdomsCore.getRegisteredModuals()) {
            if (km.getPlayerData() != null) {
                HashMap<String, Object> extraData = new HashMap<String, Object>();
                for (Field f : km.getPlayerData().getDeclaredFields()) {
                    try {
                        if (!f.isAccessible()) {
                            f.setAccessible(true);
                        }
                        if (f.isAnnotationPresent(SaveData.class)) {
                            if (SaveType.Saveable.class.isAssignableFrom(f.getType())) {
                                SaveType.NativeType ntv = (SaveType.NativeType) f.getType().getMethod("toJsonObject").invoke(f.get(pd));
                                extraData.put(f.getName(), new SaveType.SaveAttr(ntv.getClass(), ntv));
                            } else {
                                boolean found = false;
                                for (Class<? extends NativeType> c : KingdomsCore.getNativeTypes()) {
                                    for (Constructor<?> ctr : c.getDeclaredConstructors()) {
                                        if (Arrays.asList(ctr.getParameterTypes()).contains(f.getType())) {
                                            extraData.put(f.getName(), new SaveType.SaveAttr(c, ctr.newInstance(f.get(pd))));
                                            found = true;
                                        }
                                    }
                                }
                                if (!found) {
                                    extraData.put(f.getName(), f.get(pd));
                                }
                            }
                        }
                    } catch (SecurityException | IllegalArgumentException | NoSuchMethodException | InstantiationException |
                            IllegalAccessException | InvocationTargetException ex) {
                        Logger.getLogger(DataLoadHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                jpd.getModualData().put(km.getModualName(), extraData);
            }
        }
        return DBmanager.saveObj(jpd, new File(KingdomsCore.getPlugin().getDataFolder() + DBmanager.getFileSep() + "playerdata"),
                pd.getPlayer().getUniqueId().toString() + ".playerdata");
    }

    public static PlayerData LoadPlayerData(Player p) {
        Object jpd = DBmanager.loadObj(JsonPlayerData.class, new File(KingdomsCore.getPlugin().getDataFolder()
                + DBmanager.getFileSep() + "playerdata" + DBmanager.getFileSep() + p.getUniqueId().toString() + ".playerdata"));
        if (jpd instanceof JsonPlayerData) {
            PlayerData pd = ((JsonPlayerData) jpd).toJavaObject();//TODO make this method have content
            pd.setPlayer(p);
            return pd;
        } else {
            return new PlayerData(p);
        }
    }
}
