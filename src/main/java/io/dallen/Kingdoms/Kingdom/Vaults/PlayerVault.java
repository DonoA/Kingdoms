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
package io.dallen.Kingdoms.Kingdom.Vaults;

import io.dallen.Kingdoms.Storage.JsonClasses.JsonPlayerData;
import io.dallen.Kingdoms.Storage.JsonClasses.JsonPlayerVault;
import io.dallen.Kingdoms.Storage.SaveType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class PlayerVault implements Vault, SaveType.Saveable{
    @Getter
    private Player Owner;
    
    @Getter
    private double uniqueSize;
    
    @Getter
    private int fullSlots;
    
    @Getter
    private int capacity;
    
    @Getter
    private int amountFull;
    
    private ItemStack[] storage;
    
    @Override
    public boolean SendToPlayer(Player p){
        return true;
    }
    
    @Override
    public boolean CanOpen(Player p){
        return p.equals(Owner);
    }
    
    
    public PlayerVault(Player p, int size){
        this.uniqueSize = size;
        this.Owner = p;
        this.storage = new ItemStack[size];
    }
    
    @Override
    public JsonPlayerVault toJsonObject(){
        throw new UnsupportedOperationException();
    }
}
