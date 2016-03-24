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
