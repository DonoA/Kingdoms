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

import com.google.common.primitives.Ints;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.WallSystem;
import io.dallen.Kingdoms.Kingdom.WallSystem.Wall;
import io.dallen.Kingdoms.Kingdom.WallSystem.WallType;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import io.dallen.Kingdoms.Util.LocationUtil;
import io.dallen.Kingdoms.Util.LogUtil;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Location;
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
                final Player p = e.getPlayer();
                new Thread(new Runnable(){
                    @Override
                    public void run(){ // Calculate redstone torch plot
                        double currMin = 51;
                        Wall startWall = null;
                        for(Plot plt : Plot.getAllPlots()){
                            if(plt instanceof Wall){
                                if(th.getCenter().distance(plt.getCenter()) < currMin){
                                    currMin = th.getCenter().distance(plt.getCenter());
                                    startWall = (Wall) plt;
                                }
                            }
                        }
                        if(startWall == null){
                            p.sendMessage("Could not calculate plot");
                            return;
                        }
                        ArrayList<Wall> corners = new ArrayList<Wall>();
                        corners.add(startWall);
                        //Redstone direction check needs to be first
                        boolean complete = false;
                        boolean direction = true;//true:X, false:Z
                        //Start with X direction
                        //Then check Z
                        //continue until the found point is also the start point
                        Point last; // the last confirmed point
                        Point current = LocationUtil.asPoint(startWall.getCenter()); // the current point being delt with
                        Wall currentWall = startWall;
                        while(!complete){
                            last = current; // cycle points
                            current = null;
                            for(int i = 1; i<64 && current == null; i++){
                                Location neg = new Location(startWall.getCenter().getWorld(), (direction ? last.getX() - i : last.getX()), startWall.getCenter().getBlockY(), (!direction ? last.getY() - i : last.getY())); // the point in the negative direction
                                Location pos = new Location(startWall.getCenter().getWorld(), (direction ? last.getX() + i : last.getX()), startWall.getCenter().getBlockY(), (!direction ? last.getY() + i : last.getY())); // the point in the positive direction
                                Plot negP = Plot.inPlot(neg);
                                Plot posP = Plot.inPlot(pos); 
                                if(negP instanceof Wall && (((Wall) negP).getType().equals(WallType.CORNER)|| ((Wall) negP).getType().equals(WallType.TOWER))){ // Test in the negative direction
                                    current = LocationUtil.asPoint(negP.getCenter());
                                    currentWall = (Wall) negP;
                                    if(currentWall.equals(startWall)){// If the found point is the start point
                                        complete = true;
                                    }
                                }
                                if(posP instanceof Wall && (((Wall) posP).getType().equals(WallType.CORNER)|| ((Wall) posP).getType().equals(WallType.TOWER))){
                                    if(current != null){ // If a point has already been found
                                        p.sendMessage("Plot cannot be deturmined, please use the redstone dust method");
                                        return;
                                    }
                                    if(!complete){ // If the loop is complete
                                        current = LocationUtil.asPoint(negP.getCenter());
                                        currentWall = (Wall) negP;
                                        if(currentWall.equals(startWall)){// If the found point is the start point
                                            complete = true;
                                        }
                                    }
                                }
                            }
                            if(current == null /*|| corners.contains(last)*/){ // If the test failed to located the shape
                                p.sendMessage("could not calculate municipal base");
                                return;
                            }
                            if(!complete){//dont add the start point into the list again
                                corners.add(currentWall);
                            }
                            direction = !direction;
                        }
                        LogUtil.printDebug(Arrays.toString(corners.toArray()));
                        int[] Xs = new int[corners.size()];
                        int[] Zs = new int[corners.size()];
                        int i = 0;
                        for(Wall w : corners){
                            Xs[i] = w.getCenter().getBlockX();
                            Zs[i] = w.getCenter().getBlockZ();
                            i++;
                        }
                        th.createMucicpal();
                        LogUtil.printDebug((th.getMunicipal() != null ? "calculated municipal base" : "could not calculate municipal base"));
                        Municipality newMunicipal = th.getMunicipal();
                        for(Plot p : Plot.getAllPlots()){
                            if(newMunicipal.getBase().intersects(p.getBase().getBounds2D())){
                                newMunicipal.getStructures().get(p.getClass()).add(p);
                            }
                        }
                    }
                }).start();
            }else if(e.getName().equalsIgnoreCase("Create Kingdom")){
                Plot p = (Plot) e.getData();
                p.getMunicipal().createKingdom();
            }
        }
    }
    
}
