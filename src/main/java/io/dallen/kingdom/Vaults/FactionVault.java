/*
 * This file is part of Kingdom.
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
package io.dallen.kingdom.Vaults;

import io.dallen.kingdom.Faction;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class FactionVault implements Vault{
    @Getter
    private Faction Owner;
    
    @Getter
    private int Size;
    
    public void SendToPlayer(Player p){
        
    }
    
    public boolean CanOpen(Player p){
        return true;
    }
}
