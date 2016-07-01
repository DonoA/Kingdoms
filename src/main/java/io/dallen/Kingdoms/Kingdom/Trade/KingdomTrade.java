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
package io.dallen.Kingdoms.Kingdom.Trade;

import io.dallen.Kingdoms.Kingdom.Kingdom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class KingdomTrade extends Trade{
    private ArrayList<KingdomTrade> allActiveKingdomTrades = new ArrayList<KingdomTrade>();
    private ArrayList<ItemStack> Items1 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Items2 = new ArrayList<ItemStack>();
    private Kingdom kingdom1;
    private Kingdom kingdom2;
    private Date expires;
    private int interval;
    private boolean active;
    
    public KingdomTrade(ItemStack[] Items1, ItemStack[] Items2, Kingdom kingdom1, Kingdom kingdom2){
        super(Items1, Items2);
        this.kingdom1 = kingdom1;
        this.kingdom2 = kingdom2;
        this.active = false;
    }
    
    public boolean completeTrade(){
        //Check if Items can be moved and move said items if they can
        //also check if the trade has expired
        return true;
    }
    
}
