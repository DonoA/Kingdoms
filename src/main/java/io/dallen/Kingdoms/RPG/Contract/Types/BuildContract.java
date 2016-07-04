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
package io.dallen.Kingdoms.RPG.Contract.Types;

import io.dallen.Kingdoms.Kingdom.Structures.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Blueprint;
import io.dallen.Kingdoms.RPG.Contract.Contract;
import io.dallen.Kingdoms.Util.ChestGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;

/**
 *
 * @author Donovan Allen
 */
public class BuildContract implements Contract, Listener{
    
    @Getter
    private ContractTarget contractTarget;
    
    @Getter
    private Player contractor;
    
    @Getter
    private Object contractee;
    
    @Getter
    private RewardType rewardType;
    
    @Getter
    private Object reward;
    
    @Getter
    private Blueprint building;
    
    @Getter
    private Plot plot;
    
    @Getter @Setter
    private boolean workerFinished;
    
    @Getter @Setter
    private boolean contractorFinished;
    
    public BuildContract(Player contractor, ChestGUI.OptionClickEvent e){
        this.contractor = contractor;
        Plot p = Plot.inPlot(e.getPlayer().getLocation());
        if(p != null){
//            new ChestGUI("Select Reward Type", InventoryType.HOPPER, this){{
                
//            }};
        }else{
            e.getPlayer().sendMessage("You must be in a plot to create this type of contract");
        }
    }
    
    @Override
    public boolean isFinished(){
        return false;
    }
    
}
