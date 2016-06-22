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
package io.dallen.Kingdoms.Kingdom.Structures.Types;

import io.dallen.Kingdoms.Handlers.BuildingHandler;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allows the kingdom to train its NPCs
 * 
 * @author donoa_000
 */
public class TrainingGround extends Plot{
    
    private int capacity;
    
    private TrainerStats Trainers;
    
    private TraineeStats Trainees;
    
    private int trainingSpeed;
    
    private int trainingArea;
    
    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;

        
    
    public TrainingGround(Plot p) {
        super(p);
        EditPlot = new ChestGUI("Training Ground", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }};
        
        BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Light Trainging Ground");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Dark Training Ground");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Other");
        }};
    }
    
    private static class TrainerStats{
        private int Archers;
        private int Infantry;
        private int Cavalry;
        private int Generals;
    }
    
    private static class TraineeStats{
        private int Archers;
        private int Infantry;
        private int Cavalry;
        private int Generals;
    }
    
    public static enum SoldierType{
        ARCHER, INFANTRY, CAVALRY, GENERAL
    }
    
    public void trainArcher(int number){
        this.Trainees.Archers += number;
        
    }
    
    public void trainInfantry(int number){
        this.Trainees.Infantry += number;
        
    }
    
    public void trainCavalry(int number){
        this.Trainees.Cavalry += number;
        
    }
    
    public void trainGeneral(int number){
        this.Trainees.Generals += number;
        
    }
    
    @Override
    public void sendEditMenu(Player p){ //TODO make this handle much better
        if(super.getMunicipal() != null){
            EditPlot.setOption(2, new ItemStack(Material.ENCHANTED_BOOK), "Train Archer")
                    .setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Train Infantry")
                    .setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Train Cavalry")
                    .setOption(5, new ItemStack(Material.ENCHANTED_BOOK), "Train General");
        }else{
            EditPlot.removeOption(2).removeOption(3).removeOption(4).removeOption(5);
        }
        
        EditPlot.sendMenu(p);
    }
    
    public class MenuHandler implements OptionClickEventHandler{

        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("Training Ground")){
                if(e.getName().equalsIgnoreCase("Train Archer")){
                    Main.getNPCs().spawnSoldier("Dallen", getCenter(), getMunicipal(), SoldierType.ARCHER);
                }else if(e.getName().equalsIgnoreCase("Train Infantry")){
                    Main.getNPCs().spawnSoldier("Dallen", getCenter(), getMunicipal(), SoldierType.INFANTRY);
                }else if(e.getName().equalsIgnoreCase("Train Cavalry")){
                    Main.getNPCs().spawnSoldier("Dallen", getCenter(), getMunicipal(), SoldierType.CAVALRY);
                }else if(e.getName().equalsIgnoreCase("Train General")){
                    Main.getNPCs().spawnSoldier("Dallen", getCenter(), getMunicipal(), SoldierType.GENERAL);
                }else{
                    BuildingHandler.chestBuildOptions(e, BuildMenu, TrainingGround.this);
                }
            }
        }
    }
}
