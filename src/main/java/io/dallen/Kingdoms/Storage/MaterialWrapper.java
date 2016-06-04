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

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class MaterialWrapper {
    @Getter
    private int StackSize;
    @Getter @Setter
    private Material Material;
    @Getter
    private byte Data;
    
    public MaterialWrapper(ItemStack is){
        this.StackSize = is.getAmount();
        this.Material = is.getType();
        this.Data = is.getData().getData();
    }
    
    public MaterialWrapper(Material mat, int number){
        this.Material = mat;
        this.StackSize = number;
    }
    
    public void addToStack(int amount){
        StackSize += amount;
    }
    
    public void removeFromStack(int amount){
        StackSize -= amount;
    }
    
    public ItemStack asBukkitItem(){
        ItemStack is = new ItemStack(Material);
        is.setAmount((StackSize > 64 ? 64 : StackSize));
        is.getData().setData(Data);
        return is;
    }
    
    public String toString(){
        return Material.name() + ":" + StackSize;
    }
}
