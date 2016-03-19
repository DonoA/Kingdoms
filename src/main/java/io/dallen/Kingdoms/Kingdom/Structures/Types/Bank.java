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
package io.dallen.Kingdoms.Kingdom.Structures.Types;

import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * Allows the kingdom to store its subjects’ wealth safely
 * 
 * @author donoa_000
 */
public class Bank extends Plot{
    @Getter
    private int securityLevel;
    @Getter
    private int vaultNumber;
    @Getter
    private HashMap<Player, BuildingVault> vaults = new HashMap<Player, BuildingVault>();
    @Getter
    private boolean MunicipalBank;
    @Getter
    private boolean KingdomBank;
    
    public Bank(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
    }

}
