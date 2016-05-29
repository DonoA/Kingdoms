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

import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import static io.dallen.Kingdoms.Kingdom.Structures.Structure.BuildMenu;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.LogUtil;
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
    private static ChestGUI EditPlot;

    static{
        EditPlot = new ChestGUI("Training Ground", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Upgrade");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
        }};
    }
    
    public TrainingGround(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
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
    public void sendEditMenu(Player p){
        if(super.getMunicipal() == null){
            EditPlot.setOption(2, new ItemStack(Material.ENCHANTED_BOOK), "Train Archer");
            EditPlot.setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Train Infantry");
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Train Cavalry");
            EditPlot.setOption(5, new ItemStack(Material.ENCHANTED_BOOK), "Train General");
        }
        EditPlot.setMenuData(this);
        EditPlot.sendMenu(p);
    }
    
    public static class MenuHandler implements ChestGUI.OptionClickEventHandler{

        @Override
        public void onOptionClick(ChestGUI.OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("Wall")){
                if(e.getName().equalsIgnoreCase("Build")){
                    LogUtil.printDebug(((Structure) e.getMenuData()).getMunicipal());
                    LogUtil.printDebug(((Structure) e.getMenuData()).getMunicipal().getStructures().toString());
                    if(((Structure) e.getMenuData()).getMunicipal() != null && 
                        !((Structure) e.getMenuData()).getMunicipal().getStructures().get(BuildersHut.class).isEmpty()){
                        BuildMenu.setMenuData(e.getMenuData());
                        e.setNext(BuildMenu);
                    }else{
                        e.getPlayer().sendMessage("You have no NPCs to build this!");
                    }
                }else if(e.getName().equalsIgnoreCase("Erase")){
                    e.getPlayer().sendMessage("Default option called");
                }else if(e.getName().equalsIgnoreCase("Demolish")){
                    e.getPlayer().sendMessage("Default option called");
                }
            }
        }
    }
}
