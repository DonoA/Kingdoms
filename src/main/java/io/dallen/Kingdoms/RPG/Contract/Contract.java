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
package io.dallen.Kingdoms.RPG.Contract;

import io.dallen.Kingdoms.Util.ChestGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public interface Contract {
    
    public boolean isFinished();
    
    public ContractTarget getContractTarget();
    
    public Player getContractor();
    
    public Object getContractee();
    
    public RewardType getRewardType();
    
    public Object getReward();
    
    public boolean isContractorFinished();
    
    public boolean isWorkerFinished();
    
    public ItemStack getContractItem();
    
    public void interact(PlayerInteractEvent e);
    
    public static enum ContractTarget{
        PLAYER, NPC, BOTH
    }
    
    public static enum RewardType{
        PLOT, GOLD, ITEM
    }
}
