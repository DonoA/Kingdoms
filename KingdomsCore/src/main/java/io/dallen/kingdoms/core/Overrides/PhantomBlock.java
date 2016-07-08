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
package io.dallen.kingdoms.core.Overrides;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import io.dallen.kingdoms.core.KingdomsCore;
import io.dallen.kingdoms.utilities.Blueprint.BlueBlock;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author Donovan Allen
 */
public class PhantomBlock{
    
    private static HashMap<Location, PhantomBlock> openBlocks = new HashMap<Location, PhantomBlock>();
    
    private Location bLoc;
    
    private BlueBlock block;
    
    private ArrayList<Player> viewers = new ArrayList<Player>();
    
    /**
     * do not add players that do not have this chunk rendered, there may be unexpected results
     */
    public PhantomBlock(BlueBlock b, Location l, Player...p){
        this.bLoc = l;
        this.block = b;
        this.viewers.addAll(Arrays.asList(p));
        openBlocks.put(l, this);
        sendToPlayers();
    }
    
    public void sendToPlayers(){
        PacketContainer updatePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE, false);
        updatePacket.getBlockPositionModifier().write(0, BlockPosition.getConverter().getSpecific(bLoc));
        updatePacket.getBlockData().write(0, WrappedBlockData.createData(block.getBlock(), block.getData()));
        try {
            for(Player p : viewers){
                KingdomsCore.getProtocolManager().sendServerPacket(p, updatePacket);
            }
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PhantomBlock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeViewer(Player p){
        viewers.remove(p);
        PacketContainer updatePacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE, false);
        updatePacket.getBlockPositionModifier().write(0, BlockPosition.getConverter().getSpecific(bLoc));
        updatePacket.getBlockData().write(0, WrappedBlockData.createData(bLoc.getBlock().getType(), bLoc.getBlock().getData()));
        try {
            KingdomsCore.getProtocolManager().sendServerPacket(p, updatePacket);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PhantomBlock.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(viewers.isEmpty()){
            openBlocks.remove(bLoc);
        }
    }
    
    
    public static class UpdateHandler  implements Listener {
        
        @EventHandler
        public void onBlockPlace(BlockPlaceEvent e){
            if(openBlocks.containsKey(e.getBlock().getLocation())){
                e.setCancelled(true);
                openBlocks.get(e.getBlock().getLocation()).sendToPlayers();
            }
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent e){
            if(openBlocks.containsKey(e.getBlock().getLocation())){
                e.setCancelled(true);
                openBlocks.get(e.getBlock().getLocation()).sendToPlayers();
            }
        }
    }
    
}
