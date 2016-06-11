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
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import io.dallen.Kingdoms.Util.LocationUtil;
import java.awt.geom.Ellipse2D;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class TownHall extends Plot{
    
    @Getter
    private ChestGUI EditPlot;
    @Getter
    private ChestGUI BuildMenu;
    
    public TownHall(Plot p){
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
        EditPlot = new ChestGUI("Builders Hut", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Demolish");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Erase");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Build");
            setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Mount Horse");
            setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Mount Army Horse");
            setOption(5, new ItemStack(Material.ENCHANTED_BOOK), "Mount King Horse");
        }};
        EditPlot.setMenuData(this);
        BuildMenu = new ChestGUI("Build Options", 2, new MenuHandler()){{
            setOption(1*9+3, new ItemStack(Material.ENCHANTED_BOOK), "Light Builder's Hut");
            setOption(1*9+4, new ItemStack(Material.ENCHANTED_BOOK), "Dark Builder's Hut");
            setOption(1*9+5, new ItemStack(Material.ENCHANTED_BOOK), "Other");
        }};
    }
    
    @Override
    public void sendEditMenu(Player p){
        if(super.getMunicipal() == null){
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Create Municipal", this);
        }else if(super.getKingdom() == null){
            EditPlot.setOption(4, new ItemStack(Material.ENCHANTED_BOOK), "Create Kingdom", this);
        }
        EditPlot.setMenuData(this);
        EditPlot.sendMenu(p);
    }
    
    public static class MenuHandler implements OptionClickEventHandler{
        
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getName().equalsIgnoreCase("Create Municipal")){
                final Plot th = (Plot) e.getData();
                th.createMucicpal();
                th.getMunicipal().setInfluence(new Ellipse2D.Double(th.getCenter().getBlockX()-th.getMunicipal().getType().getRadius()/2, 
                            th.getCenter().getBlockZ()-th.getMunicipal().getType().getRadius()/2, th.getMunicipal().getType().getRadius(), 
                            th.getMunicipal().getType().getRadius()));
//                e.getPlayer().teleport(th.getCenter());
//                final Ellipse2D cic = th.getMunicipal().getInfluence();
//                final Rectangle bounds = th.getMunicipal().getInfluence().getBounds();
//                int Xmax = (int) bounds.getMaxX();
//                int Zmax = (int) bounds.getMaxY();
//                for(int x = (int) bounds.getMinX(); x <= Xmax; x++){
//                    for(int z = (int) bounds.getMinY(); z <= Zmax; z++){
//                        if(cic.contains(new Point(x,z)) || (cic.contains(new Point(x-1,z)) || cic.contains(new Point(x,z-1)) || cic.contains(new Point(x-1,z-1)))){
//                            Location l = new Location(th.getCenter().getWorld(), x, th.getCenter().getBlockY()-1, z);
//                            l.getBlock().setType(Material.DIAMOND_BLOCK);
//                        }
//                    }
//                }
                th.getMunicipal().setInfluenceCenter(th.getCenter());
                e.getPlayer().sendMessage("Municipal Created!");
                e.getPlayer().sendMessage("Adding Structures");
                final Player plr = e.getPlayer();
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        for(Plot p : Plot.getAllPlots()){
                            if(p.getMunicipal() == null && th.getMunicipal().getInfluence().contains(LocationUtil.asPoint(p.getCenter()))){
                                plr.sendMessage("Added a plot to the municipal");
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
            }else{
                final Plot th = (Plot) e.getData();
                BuildingHandler.chestBuildOptions(e, th.getBuildMenu());
            }
        }
    }
    
}
