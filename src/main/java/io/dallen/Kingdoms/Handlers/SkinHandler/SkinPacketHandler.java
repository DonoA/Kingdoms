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
package io.dallen.Kingdoms.Handlers.SkinHandler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import io.dallen.Kingdoms.Main;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import lombok.Getter;

/**
 *
 * @author donoa_000
 */
public class SkinPacketHandler implements Runnable{
    
    @Getter
    private PacketAdapter adapter;
    
    private volatile Collection<WrappedSignedProperty> properties;

    public SkinPacketHandler(){
        adapter = new PacketAdapter(Main.getPlugin(), PacketType.Play.Server.PLAYER_INFO){
            @Override
            public void onPacketSending(PacketEvent event){
                if(properties == null){
                    return;
                }
                PacketContainer packet = event.getPacket();
                EnumWrappers.PlayerInfoAction action = packet.getPlayerInfoAction().read(0);
                if(action != EnumWrappers.PlayerInfoAction.ADD_PLAYER){
                    return;
                }
                List<PlayerInfoData> data = packet.getPlayerInfoDataLists().read(0);
                for(PlayerInfoData pid : data){
                    WrappedGameProfile profile = pid.getProfile();
                    profile.getProperties().removeAll("textures");
                    profile.getProperties().putAll("textures", properties);
                }
            }
        };
    }
    
    @Override
    public void run(){
        WrappedGameProfile profile = WrappedGameProfile.fromPlayer(null);
        Object handle = profile.getHandle();
        Object sessionService = getSessionService();
        try{
            Method method = getFillMethod(sessionService);
            method.invoke(sessionService, handle, true);
        }catch (IllegalAccessException | InvocationTargetException ex){
            ex.printStackTrace();
            return;
        }
        profile = WrappedGameProfile.fromHandle(handle);
        properties = profile.getProperties().get("textures");
        if(properties == null){
//            SkinChanger.this.getLogger().warning("Failed to load the skin, player skins won't be affected by this plugin (Yes, that's bad).");
        }
    }
        
    private Object getSessionService(){
        Server server = Bukkit.getServer();
        try{
            Object mcServer = server.getClass().getDeclaredMethod("getServer").invoke(server);
            for (Method m : mcServer.getClass().getMethods()){
                if (m.getReturnType().getSimpleName().equalsIgnoreCase("MinecraftSessionService")){
                    return m.invoke(mcServer);
                }
            }
        }catch (Exception ex){
            throw new IllegalStateException("An error occurred while trying to get the session service", ex);
        }
        throw new IllegalStateException("No session service found :o");
    }

    private Method getFillMethod(Object sessionService){
        for(Method m : sessionService.getClass().getDeclaredMethods()){
            if(m.getName().equals("fillProfileProperties")){
                return m;
            }
        }
        throw new IllegalStateException("No fillProfileProperties method found in the session service :o");
    }
}
