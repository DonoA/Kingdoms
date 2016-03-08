/*
 * This file is part of Kingdoms.
 * 
 * Kingdom is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdom is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdom.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms;

import io.dallen.Kingdoms.Handlers.Party;
import io.dallen.Kingdoms.Roles.Role;
import io.dallen.Kingdoms.Vaults.PlayerVault;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class PlayerData {
    @Getter
    private static HashMap<Player, PlayerData> PlayerDat = new HashMap<Player, PlayerData>();
    
    @Getter @Setter
    private Role Role;
    
    @Getter @Setter
    private String Faction;
    
    @Getter @Setter
    private PlayerVault Vault;
    
    @Getter @Setter
    private Party CurrParty;
    
    @Getter @Setter
    private int Might;
    
    @Getter @Setter
    private Location Spawn;
    
    public PlayerData(){
        
    }
}
