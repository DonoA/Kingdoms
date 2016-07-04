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
package io.dallen.Kingdoms.Handlers;

import io.dallen.Kingdoms.Overrides.KingdomMaterial;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Donovan Allen
 */
public class ContractHandler implements Listener, OptionClickEventHandler{
    
    private static HashMap<String, Long> cooldown = new HashMap<String, Long>();
    
    private ChestGUI createContract = new ChestGUI("",9*1,this){{
        setOption(9*0 + 0, KingdomMaterial.DEFAULT.getItemStack(), "Build");
        setOption(9*0 + 1, KingdomMaterial.DEFAULT.getItemStack(), "Demolish");
        setOption(9*0 + 2, KingdomMaterial.DEFAULT.getItemStack(), "Gather");
        setOption(9*0 + 3, KingdomMaterial.DEFAULT.getItemStack(), "Guard");
        setOption(9*0 + 4, KingdomMaterial.DEFAULT.getItemStack(), "Patrol");
        setOption(9*0 + 5, KingdomMaterial.DEFAULT.getItemStack(), "Transport");
    }};
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(((!cooldown.containsKey(e.getPlayer().getName())) || 
                (cooldown.containsKey(e.getPlayer().getName()) && cooldown.get(e.getPlayer().getName()) < System.currentTimeMillis() - 100)) && 
                e.hasItem() && KingdomMaterial.CONTRACT_EMPTY.getItemStack().isSimilar(e.getItem()) && 
                (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))){
            cooldown.put(e.getPlayer().getName(), System.currentTimeMillis());
            createContract.sendMenu(e.getPlayer());
        }
    }
    
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void onOptionClick(ChestGUI.OptionClickEvent e){
        if(e.getName().equals(createContract.getName())){
            try {
                Class<?> contract = Class.forName("io.dallen.Kingdoms.Kingdom.RPG.Contract.Types."+e.getName().replace(" ", "").replace("'", "") + "Contract");
                contract.getConstructor(Player.class, ChestGUI.OptionClickEvent.class).newInstance(e.getPlayer(), e);
            } catch (ClassNotFoundException | IllegalArgumentException | SecurityException | NoSuchMethodException | IllegalAccessException 
                    | InvocationTargetException | InstantiationException ex) {
                ex.printStackTrace();
                e.getPlayer().sendMessage("Building name not found!");
            }
        }
    }
    
}
