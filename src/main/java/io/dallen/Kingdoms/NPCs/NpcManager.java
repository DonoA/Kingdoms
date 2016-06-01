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

import io.dallen.Kingdoms.Kingdom.Municipality;
import io.dallen.Kingdoms.Kingdom.Structures.Types.TrainingGround.SoldierType;
import io.dallen.Kingdoms.NPCs.Traits.Builder;
import io.dallen.Kingdoms.NPCs.Traits.Soldiers.Archer;
import io.dallen.Kingdoms.NPCs.Traits.Soldiers.Cavalry;
import io.dallen.Kingdoms.NPCs.Traits.Soldiers.General;
import io.dallen.Kingdoms.NPCs.Traits.Soldiers.Infantry;
import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/**
 *
 * @author donoa_000
 */
public class NpcManager {
    
    @Getter @Setter
    private NPCRegistry NPCReg;
    
    public NpcManager(){
        NPCReg = CitizensAPI.getNPCRegistry();
    }
    
    public void CreateNPCs(){
        
    }
    
    public NPC spawnBuilder(String user, Location spawn){
        NPC Builder = NPCReg.createNPC(EntityType.PLAYER, user);
        Builder.spawn(spawn);
        Builder.addTrait(new Builder());
        return Builder;
    }
    
    public NPC spawnSoldier(String user, Location spawn, Municipality owner, SoldierType type){
        NPC Soldier = NPCReg.createNPC(EntityType.PLAYER, user);
        Soldier.spawn(spawn);
        switch(type){
            case ARCHER:
                Archer archer = new Archer();
                archer.setMunicipal(owner);
                Soldier.addTrait(archer);
                break;
            case INFANTRY:
                Infantry infantry = new Infantry();
                infantry.setMunicipal(owner);
                Soldier.addTrait(infantry);
                break;
            case CAVALRY:
                Cavalry cavalry = new Cavalry();
                cavalry.setMunicipal(owner);
                Soldier.addTrait(cavalry);
                break;
            case GENERAL:
                General general = new General();
                general.setMunicipal(owner);
                Soldier.addTrait(general);
                break;
        }
        return Soldier;
    }
}
