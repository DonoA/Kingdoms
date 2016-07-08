/*
 * Copyright 2016 Donovan Allen.
 * 
 * This file is part of KingdomsCore for the Morphics Network.
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
package io.dallen.kingdoms.core;

import com.comphenix.protocol.ProtocolManager;
import io.dallen.kingdoms.core.Handlers.MultiBlocks.MultiBlock;
import io.dallen.kingdoms.core.Handlers.SkinHandler.SkinPacketHandler;
import io.dallen.kingdoms.core.NPCs.NpcManager;
import io.dallen.kingdoms.core.Storage.DataLoadHelper;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

/**
 *
 * @author Donovan Allen
 */
public class KingdomsCore extends JavaPlugin{
    
    @Getter
    private static KingdomsCore Plugin;
    
    @Getter @Setter
    private static NpcManager NPCs;
    
    @Getter
    private static Set<Class<? extends Plot>> StructureClasses;
    
    @Getter
    private static Set<Class<? extends SaveType.NativeType>> NativeTypes;
    
    @Getter
    private static ProtocolManager protocolManager;
    
    @Getter
    private static ArrayList<Kingdom> Kingdoms = new ArrayList<Kingdom>();
    
    @Getter
    private static ArrayList<Municipality> Municipals = new ArrayList<Municipality>();
    
    @Getter
    private static SkinPacketHandler skinHandler;
    
    @Override
    public void onEnable(){
        Plugin = this;
    Reflections reflections = new Reflections("io.dallen.Kingdoms");
        Set<Class<? extends Listener>> lstn = reflections.getSubTypesOf(Listener.class);
        for(Class<? extends Listener> l : lstn){
            boolean hasEventHandler = false;
            for(Method m : l.getDeclaredMethods()){
                if(m.isAnnotationPresent(EventHandler.class)){
                    hasEventHandler = true;
                }
            }
            if(hasEventHandler){
                try {
                    l.getConstructor();
                    Bukkit.getPluginManager().registerEvents((Listener) l.newInstance(), this);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException ex) {}
            }
        }
        reflections = new Reflections("io.dallen.Kingdoms.Kingdom.Structures.Types");
        StructureClasses = reflections.getSubTypesOf(Plot.class);
        reflections = new Reflections("io.dallen.Kingdoms.Storage");
        NativeTypes = reflections.getSubTypesOf(SaveType.NativeType.class);
        setupDatabase();
//        RedisManager RM = new RedisManager();
    }
    
    @Override
    public void onDisable(){
        DataLoadHelper.SaveKingdomData();
    }
    
    /*
    Kingdoms
    |   config.yml
    |
    +---multiblock
    |   \---Forge
    +---playerdata
    +---prefabs
    |   +---Armory
    |   +---Bank
    |   +---Barracks
    |   +---Blacksmith
    |   +---BuildersHut
    |   +---Castle
    |   +---Dungeon
    |   +---Farm
    |   +---Marketplace
    |   +---Stable
    |   +---Storeroom
    |   +---TownHall
    |   \---TrainingGround
    \---savedata
        +---kingdoms
        +---municipals
        \---plots
     */
    
    public static void setupDatabase(){
        File plugin = KingdomsCore.getPlugin().getDataFolder();
        for(String s : new String[] {"prefabs", "playerdata", "multiblock", "savedata"}){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + s).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + s).mkdir();
            }
        }
        for(Class<?> c : StructureClasses){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + c.getSimpleName()).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + "prefabs" + DBmanager.getFileSep() + c.getSimpleName()).mkdir();
            }
        }
        for(Class<?> c : MultiBlock.getMultiBlockClasses()){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + "multiblock" + DBmanager.getFileSep() + c.getSimpleName()).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + "multiblock" + DBmanager.getFileSep() + c.getSimpleName()).mkdir();
            }
        }
        for(String s : new String[] {"kingdoms", "municipals", "plots"}){
            if(!new File(plugin.getPath() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + s).exists()){
                new File(plugin.getPath() + DBmanager.getFileSep() + "savedata" + DBmanager.getFileSep() + s).mkdir();
            }
        }
    }
}
