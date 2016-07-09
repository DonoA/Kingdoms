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
package io.dallen.kingdoms.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dallen.kingdoms.core.Handlers.Party;
import io.dallen.kingdoms.core.Storage.JsonPlayerData;
import io.dallen.kingdoms.core.Structures.Plot;
import io.dallen.kingdoms.core.Structures.Vaults.PlayerVault;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonLocation;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
@NoArgsConstructor
public class PlayerData implements SaveType.Saveable{
    @Getter
    private static HashMap<Player, PlayerData> PlayerDat = new HashMap<Player, PlayerData>();
    
//    @Getter @Setter
//    private Role Role;
    
    @Getter @Setter
    private Kingdom Kingdom;
    
    @Getter @Setter
    private Player Player;
    
    @Getter @Setter
    private Municipality Municipal;
    
    @Getter @Setter
    private PlayerVault Vault;
    
    @Getter @Setter @JsonIgnore
    private String PartyID;
    
    @Getter @Setter
    private String title = "Testing";
    
    @Getter @Setter
    private int Might;
    
    @Getter @Setter
    private Location Spawn;
    
    
    
    @Getter
    private Rating contractorRating;
    
    @Getter
    private Rating workerRating;
    
//    @Getter
    private ArrayList<Contract> currentContracts = new ArrayList<Contract>();
    
    @Getter
    private ArrayList<Plot> Plots = new ArrayList<Plot>();
    
    @Getter
    private int gold = 0;
    
    public PlayerData(Player p){
        this.Player = p;
        Vault = new PlayerVault(p, 27);
//        muted = new MuteClass();
    }
    
    public static PlayerData getData(Player p){
        if(PlayerDat.containsKey(p)){
            return PlayerDat.get(p);
        }
        return null;
    }
    
    public void addGold(int amount){
        gold += amount;
    }
    
    public void removeGold(int amount){
        gold -= amount;
    }
    
    public Party getCurrParty(){
        if(PartyID == null){
            return null;
        }
        return Party.getParties().get(PartyID);
    }
    
//    @Override
    public JsonPlayerData toJsonObject(){
        JsonPlayerData jpd = new JsonPlayerData();
        jpd.setKingdom(Kingdom != null ? Kingdom.getKingdomID() : -1);
        jpd.setMight(Might);
        jpd.setMunicipal(Municipal != null ? Municipal.getMunicipalID() : -1);
//        jpd.setMuted(muted);
//        jpd.setRole(Role != null ? Role.getRoleName() : null);
        if(Spawn != null){
            jpd.setSpawn(new JsonLocation(Spawn));
        }else{
            jpd.setSpawn(new JsonLocation(Bukkit.getWorlds().get(0).getSpawnLocation()));
        }
        jpd.setVault(Vault.toJsonObject());
        return jpd;
    }
    
    public static enum Rating{
        VERY_NEGATIVE, NEGATIVE, POOR, NEUTRAL, FAIR, GOOD, VERY_GOOD, PERFECT
    }
}
