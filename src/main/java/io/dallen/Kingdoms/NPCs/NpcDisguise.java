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
package io.dallen.Kingdoms.NPCs;

//import java.io.PrintStream;
//import java.lang.reflect.Field;
//import java.util.Map;
//import net.minecraft.server.DataWatcher;
//import net.minecraft.server.EntityPlayer;
//import net.minecraft.server.MathHelper;
//import net.minecraft.server.NetServerHandler;
//import net.minecraft.server.Packet20NamedEntitySpawn;
//import net.minecraft.server.Packet24MobSpawn;
//import net.minecraft.server.Packet29DestroyEntity;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Server;
//import org.bukkit.craftbukkit.entity.CraftPlayer;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;

/**
 *
 * @author donoa_000
 */
public class NpcDisguise {
    
//  public void killCarcass(Player p1){
//    CraftPlayer p22 = (CraftPlayer)p1;
//    Packet29DestroyEntity p29 = new Packet29DestroyEntity(p22.getEntityId());
//    Player[] arrayOfPlayer;
//    int j = (arrayOfPlayer = Bukkit.getServer().getOnlinePlayers()).length;
//    for (int i = 0; i < j; i++){
//      Player p2 = arrayOfPlayer[i];
//      if (p1.getWorld().equals(p2.getWorld())){
//        if (!p2.getName().equals(p1.getName())){
//          ((CraftPlayer)p2).getHandle().netServerHandler.sendPacket(p29);
//          ((CraftPlayer)p2).getHandle().netServerHandler.sendPacket(p29);
//        }
//      }
//    }
//  }
//  
//  public void undisguiseToAll(Player p1)
//  {
//    CraftPlayer p22 = (CraftPlayer)p1;
//    Packet29DestroyEntity p29 = new Packet29DestroyEntity(p22.getEntityId());
//    Packet20NamedEntitySpawn p20 = new Packet20NamedEntitySpawn(p22.getHandle());
//    Player[] arrayOfPlayer;
//    int j = (arrayOfPlayer = Bukkit.getServer().getOnlinePlayers()).length;
//    for (int i = 0; i < j; i++)
//    {
//      Player p2 = arrayOfPlayer[i];
//      if (p1.getWorld().equals(p2.getWorld())) {
//        if (p2 != p1)
//        {
//          ((CraftPlayer)p2).getHandle().netServerHandler.sendPacket(p29);
//          ((CraftPlayer)p2).getHandle().netServerHandler.sendPacket(p20);
//        }
//      }
//    }
//  }
//  
//  public void disguiseToAll(Player p1)
//  {
////    Packet24MobSpawn p24 = packetMaker(p1, Byte.valueOf(((Disguise)MobDisguise.playerMobDis.get(p1.getName())).mob.id));
//    Player[] arrayOfPlayer;
//    int j = (arrayOfPlayer = Bukkit.getServer().getOnlinePlayers()).length;
//    for (int i = 0; i < j; i++)
//    {
//      Player p2 = arrayOfPlayer[i];
//      if (p1.getWorld().equals(p2.getWorld())) {
//        if (p2 != p1) {
//          ((CraftPlayer)p2).getHandle().netServerHandler.sendPacket(p24);
//        }
//      }
//    }
//  }
//  
//  public void disguisep2pToAll(Player p, String name)
//  {
//    Packet20NamedEntitySpawn p20 = packetMaker(p, name);
//    Packet29DestroyEntity p29 = new Packet29DestroyEntity(p.getEntityId());
//    
//    p.setDisplayName(name);
//    Player[] arrayOfPlayer;
//    int j = (arrayOfPlayer = Bukkit.getServer().getOnlinePlayers()).length;
//    for (int i = 0; i < j; i++)
//    {
//      Player p1 = arrayOfPlayer[i];
//      if (p.getWorld().equals(p1.getWorld())) {
//        if (p1 != p)
//        {
//          ((CraftPlayer)p1).getHandle().netServerHandler.sendPacket(p29);
//          ((CraftPlayer)p1).getHandle().netServerHandler.sendPacket(p20);
//        }
//      }
//    }
//  }
//  
//  public void undisguisep2pToAll(Player p)
//  {
//    p.setDisplayName(p.getName());
//    Packet20NamedEntitySpawn p20 = packetMaker(p, p.getName());
//    Packet29DestroyEntity p29 = new Packet29DestroyEntity(p.getEntityId());
//    Player[] arrayOfPlayer;
//    int j = (arrayOfPlayer = Bukkit.getServer().getOnlinePlayers()).length;
//    for (int i = 0; i < j; i++)
//    {
//      Player p1 = arrayOfPlayer[i];
//      if (p1 != p)
//      {
//        ((CraftPlayer)p1).getHandle().netServerHandler.sendPacket(p29);
//        ((CraftPlayer)p1).getHandle().netServerHandler.sendPacket(p20);
//      }
//    }
//  }
//  
//  public Packet20NamedEntitySpawn packetMaker(Player p, String name)
//  {
//    Location loc = p.getLocation();
//    Packet20NamedEntitySpawn packet = new Packet20NamedEntitySpawn();
//    packet.a = p.getEntityId();
//    packet.b = name;
//    packet.c = ((int)loc.getX());
//    packet.c = MathHelper.floor(loc.getX() * 32.0D);
//    packet.d = MathHelper.floor(loc.getY() * 32.0D);
//    packet.e = MathHelper.floor(loc.getZ() * 32.0D);
//    packet.f = ((byte)(int)((int)loc.getYaw() * 256.0F / 360.0F));
//    packet.g = ((byte)(int)(loc.getPitch() * 256.0F / 360.0F));
//    packet.h = p.getItemInHand().getTypeId();
//    return packet;
//  }
//  
//  public Packet24MobSpawn packetMaker(Player p1, Byte id)
//  {
//    DataWatcher tmp = null;
//    if (MobDisguise.data.get(p1.getName()) == null)
//    {
//      tmp = new DataWatcher();
//      MobDisguise.data.put(p1.getName(), tmp);
//    }
//    else
//    {
//      tmp = (DataWatcher)MobDisguise.data.get(p1.getName());
//    }
//    Location loc = p1.getLocation();
//    Packet24MobSpawn packet = new Packet24MobSpawn();
//    packet.a = ((CraftPlayer)p1).getEntityId();
//    packet.b = id.byteValue();
//    packet.c = MathHelper.floor(loc.getX() * 32.0D);
//    packet.d = MathHelper.floor(loc.getY() * 32.0D);
//    packet.e = MathHelper.floor(loc.getZ() * 32.0D);
//    packet.f = ((byte)(int)((int)loc.getYaw() * 256.0F / 360.0F));
//    packet.g = ((byte)(int)(loc.getPitch() * 256.0F / 360.0F));
//    try
//    {
//      Field datawatcher = packet.getClass().getDeclaredField("i");
//      datawatcher.setAccessible(true);
//      datawatcher.set(packet, tmp);
//      datawatcher.setAccessible(false);
//    }
//    catch (Exception e)
//    {
//      System.out.println("[MobDisguise] Error making packet?!");
//      return null;
//    }
//    Field datawatcher;
//    return packet;
//  }
}