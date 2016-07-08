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
package io.dallen.kingdoms.warcraft.Trade;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Donovan Allen
 */
public class PlayerTrade extends Trade{
    
    private ArrayList<Object> Items1 = new ArrayList<Object>();
    private ArrayList<Object> Items2 = new ArrayList<Object>();
    private Player player1;
    private Player player2;
    private boolean readyToComplete = false;
    
    public PlayerTrade(ItemStack[] Items1, ItemStack[] Items2, Player player1, Player player2){
        super(Items1, Items2);
        this.player1 = player1;
        this.player2 = player2;
    }
    
    public boolean completeTrade(){
        //Check if Items can be moved and move said items if they can
        //also check if the trade has expired
        return true;
    }
    
}
