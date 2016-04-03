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
package io.dallen.Kingdoms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.dallen.Kingdoms.Handlers.Party;
import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Roles.Role;
import io.dallen.Kingdoms.Commands.MuteCommand.MuteClass;
import io.dallen.Kingdoms.Kingdom.Kingdom;
import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Vaults.PlayerVault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
@NoArgsConstructor
public class PlayerData {
    @Getter
    private static HashMap<Player, PlayerData> PlayerDat = new HashMap<Player, PlayerData>();
    
    @Getter @Setter
    private Role Role;
    
    @Getter @Setter
    private Kingdom Kingdom;
    
    @Getter @Setter
    private Municipality Municipal;
    
    @Getter @Setter
    private PlayerVault Vault;
    
    @Getter @Setter @JsonIgnore
    private String PartyID;
    
    @Getter @Setter
    private int Might;
    
    @Getter @Setter
    private Location Spawn;
    
    @Getter @Setter
    private MuteClass muted;
    
    @Getter
    private ArrayList<Plot> Plots = new ArrayList<Plot>();
    
    public static PlayerData getData(Player p){
        if(PlayerDat.containsKey(p)){
            return PlayerDat.get(p);
        }
        return null;
    }
    
    public Party getCurrParty(){
        if(PartyID == null){
            return null;
        }
        return Party.getParties().get(PartyID);
    }
    
    
}
