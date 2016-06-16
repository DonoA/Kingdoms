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
package io.dallen.Kingdoms.Handlers;

import com.google.common.primitives.Ints;
import io.dallen.Kingdoms.Handlers.MultiBlocks.Forge;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Util.LogUtil;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Blueprint;
import io.dallen.Kingdoms.Kingdom.Structures.Contract;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import io.dallen.Kingdoms.Kingdom.Structures.Structure;
import io.dallen.Kingdoms.Kingdom.Structures.Types.BuildersHut;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.Wall;
import io.dallen.Kingdoms.Kingdom.Structures.Types.WallSystem.WallType;
import io.dallen.Kingdoms.Main;
import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Util.ChestGUI;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEvent;
import io.dallen.Kingdoms.Util.ChestGUI.OptionClickEventHandler;
import io.dallen.Kingdoms.Util.LocationUtil;
import io.dallen.Kingdoms.Util.PermissionManager;
import java.awt.Point;
import java.awt.Polygon;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class MultiBlockHandler implements Listener{
    
    private ChestGUI NewPlotMenu;
    
    private ChestGUI ViewPlotMenu;
    
    private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();
    
    @Getter
    private static MBOptions optionHandler;
    
    public MultiBlockHandler(){
        optionHandler = new MBOptions();
        NewPlotMenu = new ChestGUI("New Plot", InventoryType.HOPPER, optionHandler) {{
            setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", "");
            setOption(3, new ItemStack(Material.ENCHANTED_BOOK), "Cancel Plot Claim", "");
        }};
        
        ViewPlotMenu = new ChestGUI("Plot Info", InventoryType.HOPPER, optionHandler) {{
            setOption(2, new ItemStack(Material.ENCHANTED_BOOK), "No current contracts avalible", "");
        }};
        
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(WallSystem.Wall.getDamageBars().contains(e.getPlayer().getName())){
            final PlayerMoveEvent ev = e;
            new Thread(new Runnable(){
                @Override
                public void run(){
                    for(Plot p : Plot.getAllPlots()){
                        if(p instanceof Wall){
                            Wall w = (Wall) p;
                            if(w.getCurrInteracters().contains(ev.getPlayer()) && ev.getTo().distance(w.getCenter()) > 20){
                                w.getCurrInteracters().remove(ev.getPlayer());
                                WallSystem.Wall.getDamageBars().remove(ev.getPlayer().getName());
                                w.getDamageBar().removePlayer(ev.getPlayer());
                            }
                        }
                    }
                }
            }).start();
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e){
        if((!cooldown.containsKey(e.getPlayer())) || 
              (cooldown.containsKey(e.getPlayer()) && cooldown.get(e.getPlayer()) < System.currentTimeMillis() - 100)){
               cooldown.put(e.getPlayer(), System.currentTimeMillis());
            if(e.hasBlock()){
                Plot p = Plot.inPlot(e.getClickedBlock().getLocation());
                if(p != null){
                    if(p instanceof Wall && e.hasItem() && e.getAction().equals(Action.LEFT_CLICK_BLOCK) && 
                            !e.getPlayer().hasPermission(PermissionManager.getBuildPermission())){
                        e.setCancelled(true);
                        Wall w = (Wall) p;
                        if(e.getItem().getType().name().contains("PICKAXE")){
                            if(!w.getCurrInteracters().contains(e.getPlayer())){
                                w.getCurrInteracters().add(e.getPlayer());
                                if(!WallSystem.Wall.getDamageBars().contains(e.getPlayer().getName())){
                                    WallSystem.Wall.getDamageBars().add(e.getPlayer().getName());
                                }
                                w.getDamageBar().addPlayer(e.getPlayer());
                            }
                            w.damage();
                        }else if(e.getItem().getType().equals(Material.DIAMOND_HOE)){
                            if(!w.getCurrInteracters().contains(e.getPlayer())){
                                w.getCurrInteracters().add(e.getPlayer());
                                if(!WallSystem.Wall.getDamageBars().contains(e.getPlayer().getName())){
                                    WallSystem.Wall.getDamageBars().add(e.getPlayer().getName());
                                }
                                w.getDamageBar().addPlayer(e.getPlayer());
                            }
                            w.repair();
                        }
                    }
                }
            }
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.hasItem() && e.getItem().getType().equals(Material.FLINT_AND_STEEL)){
                final Location l = e.getClickedBlock().getLocation();
                final Player p = e.getPlayer();
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        Blueprint baseForm = Forge.getBasicForm().clone();
                        //Calculate multiblock
                    }
                }).start();
            }
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)){
                if(!e.hasItem()){
                    if(e.hasBlock()){
                        Block b = e.getClickedBlock();
                        if(b.getType().equals(Material.REDSTONE_TORCH_ON)){ // TESTING: REDSTONE_TORCH_ON
                            final Location l = b.getLocation();
                            final Player p = e.getPlayer();
                            p.sendMessage("Calculating Plot...");
                            new Thread(new Runnable(){
                                @Override
                                public void run(){ // Calculate redstone torch plot
                                    long Start = System.currentTimeMillis();
                                    ArrayList<Point> corners = new ArrayList<Point>();
                                    Point start = LocationUtil.asPoint(l);
                                    corners.add(start);
                                    //Redstone direction check needs to be first
                                    boolean complete = false;
                                    boolean direction = true;//true:X, false:Z
                                    //Start with X direction
                                    //Then check Z
                                    //continue until the found point is also the start point
                                    Point last; // the last confirmed point
                                    Point current = start; // the current point being delt with
                                    while(!complete){
                                        last = current; // cycle points
                                        current = null;
                                        for(int i = 1; i<64 && current == null; i++){
                                            Location neg = new Location(l.getWorld(), (direction ? last.getX() - i : last.getX()), 
                                                    l.getBlockY(), (!direction ? last.getY() - i : last.getY())); // the point in the negative direction
                                            Location pos = new Location(l.getWorld(), (direction ? last.getX() + i : last.getX()), 
                                                    l.getBlockY(), (!direction ? last.getY() + i : last.getY())); // the point in the positive direction
                                            if(neg.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){ // Test in the negative direction
                                                current = LocationUtil.asPoint(neg);
                                                if(current.equals(start)){// If the found point is the start point
                                                    complete = true;
                                                }
                                            }
                                            if(pos.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){
                                                if(current != null){ // If a point has already been found
                                                    p.sendMessage("Plot cannot be determined, please use the redstone dust method");
                                                    return;
                                                }
                                                if(!complete){ // If the loop is complete
                                                    current = new Point(pos.getBlockX(), pos.getBlockZ());
                                                    if(current.equals(start)){
                                                        complete = true;
                                                    }
                                                }
                                            }
                                        }
                                        if(current == null || Start + 1000 < System.currentTimeMillis()){ // If the test failed to located the shape
                                            p.sendMessage("Could not calculate plot");
                                            return;
                                        }
                                        if(!complete){//dont add the start point into the list again
                                            corners.add(current);
                                        }
                                        direction = !direction;
                                    }
                                    LogUtil.printDebug(Arrays.toString(corners.toArray()));
                                    int[] Xs = new int[corners.size()];
                                    int[] Zs = new int[corners.size()];
                                    int i = 0;
                                    for(Point p : corners){
                                        Xs[i] = (int) p.getX();
                                        Zs[i] = (int) p.getY();
                                        i++;
                                    }
                                    Plot NewPlot = new Plot(new Polygon(Xs, Zs, corners.size()), 
                                            LocationUtil.asLocation(LocationUtil.calcCenter(corners.toArray(new Point[corners.size()])), 
                                            l.getWorld(), l.getBlockY()), p, null);
                                    for(Plot plot : Plot.getAllPlots()){
                                        if(plot.getCenter().equals(NewPlot.getCenter())){
                                            if(plot.getOwner().equals(p)){
                                                p.sendMessage("You already own this plot!");
                                            }else{
                                                p.sendMessage("This plot has already been claimed!");
                                            }
                                            return;
                                        }
                                    }
                                    if(NewPlot.isValid()){
                                        NewPlotMenu.setOption(1, new ItemStack(Material.ENCHANTED_BOOK), "Confirm and Claim Plot", NewPlot, "Plot size: " + -1 + ", " + -1).sendMenu(p);
                                    }else{
                                        p.sendMessage("Invalid Plot");
                                    }
                                }
                            }).start();
                        }
                    }
                }else if(e.getItem().hasItemMeta()){
                    if(e.getItem().getItemMeta().hasDisplayName() && e.getItem().getItemMeta().getDisplayName().equalsIgnoreCase("Multi Tool")){
                        Plot p = Plot.inPlot(e.getPlayer().getLocation());
                        if(p==null){
                            return;
                        }
                        if(p.getOwner().equals(e.getPlayer())){
                            p.sendEditMenu(e.getPlayer());
                        }else{
                            int loc = 0;
                            for(Contract ct : p.getContracts()){
                                ViewPlotMenu.setOption(loc, new ItemStack(Material.ENCHANTED_BOOK), ct.getMessage(), ct.getPay(), ct.getType().getName());
                                loc++;
                            }
                            ViewPlotMenu.sendMenu(e.getPlayer());
                        }
                    }
                }
            }
        }
    }
    
    
    
    
    
    public static class MBOptions implements OptionClickEventHandler{
    
        @Override
        public void onOptionClick(OptionClickEvent e){
            if(e.getMenuName().equalsIgnoreCase("New Plot")){
                if(e.getName().equalsIgnoreCase("Confirm and Claim Plot")){
                    final Plot p = (Plot) e.getData();
                    Plot.getAllPlots().add(p);
                    PlayerData pd = PlayerData.getData(e.getPlayer());
                    pd.getPlots().add(p);
                    e.getPlayer().sendMessage("You have claimed this plot");
                    e.getPlayer().sendMessage("Setting base");
                    final Polygon bounds = p.getBase();
                    for(int i = 0; i < p.getBase().npoints; i++){
                        Location torch = LocationUtil.asLocation(new Point(p.getBase().xpoints[i], p.getBase().ypoints[i]), 
                                p.getCenter().getWorld(), p.getCenter().getBlockY());
                        if(torch.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)){
                            final Location t = torch;
                            Bukkit.getScheduler().runTask(Main.getPlugin(), new Runnable(){
                                @Override
                                public void run() {
                                    t.getBlock().breakNaturally();
                                }
                            });
                        }
                    }
                    int Xmax = Ints.max(bounds.xpoints);
                    int Zmax = Ints.max(bounds.ypoints);
                    for(int x = Ints.min(bounds.xpoints); x <= Xmax; x++){
                        for(int z = Ints.min(bounds.ypoints); z <= Zmax; z++){
                            if(bounds.contains(new Point(x,z)) || (bounds.contains(new Point(x-1,z)) || bounds.contains(new Point(x,z-1)) || bounds.contains(new Point(x-1,z-1)))){
                                Location l = new Location(p.getCenter().getWorld(), x, p.getCenter().getBlockY()-1, z);
                                l.getBlock().setType(Material.DIRT);
                                l.getBlock().setData((byte) 1);
                            }
                        }
                    }
                    for(Municipality m : Municipality.getAllMunicipals()){
                        if(m.getInfluence().intersects(p.getBase().getBounds2D())){
                            p.setMunicipal(m);
                            m.addStructure(p);
                        }
                    }
                }
            }
        }
    }
}
