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

import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import java.awt.geom.Ellipse2D;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class TownHall extends Plot{
    
    static{
        EditPlot.setName("Town Hall");
        EditPlot.setHandler(new MenuHandler());
    }
    
    public TownHall(Plot p){
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
    }
    
    @Override
    public void sendEditMenu(Player p){
        if(super.getMunicipal() == null){
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Create Municipal", this);
        }else if(super.getKingdom() == null){
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Create Kingdom", this);
        }
        super.sendEditMenu(p);
    }
    
    public static class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getName().equalsIgnoreCase("Create Municipal")){
                final Plot th = (Plot) e.getData();
                th.createMucicpal();
                th.getMunicipal().setInfluence(new Ellipse2D.Double(th.getCenter().getBlockX(), th.getCenter().getBlockZ(), 50, 50));
                e.getPlayer().sendMessage("Municipal Created!");
                e.getPlayer().sendMessage("Adding Structures");
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        for(Plot p : Plot.getAllPlots()){
                            if(p.getMunicipal() == null && th.getMunicipal().getInfluence().intersects(p.getBase().getBounds())){
                                th.getMunicipal().addStructure(p);
                            }
                        }
                    }
                }).start();
            }else if(e.getName().equalsIgnoreCase("Create Kingdom")){
                final Plot th = (Plot) e.getData();
                th.getMunicipal().createKingdom();
                th.getKingdom().setInfluence(new Ellipse2D.Double(th.getMunicipal().getInfluenceCenter().getBlockX(), 
                        th.getMunicipal().getInfluenceCenter().getBlockZ(), 250, 250));
                e.getPlayer().sendMessage("Kingdom Founded!");
                e.getPlayer().sendMessage("Adding Municipals");
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        for(Municipality m : Municipality.getAllMunicipals()){
                            if(m.getKingdom() == null && th.getMunicipal().getInfluence().intersects(m.getInfluence().getBounds())){
                                //This should ask before adding
                                th.getKingdom().getMunicipals().add(m);
                            }
                        }
                    }
                }).start();
            }
        }
    }
    
}
