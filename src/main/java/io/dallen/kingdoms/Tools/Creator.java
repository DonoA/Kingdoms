/*
 * Copyright 2017 donoa_000.
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
package io.dallen.kingdoms.Tools;

import io.dallen.kingdoms.Kingdom;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class Creator {
    
    public class Commands implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
            if(cmd.getName().equalsIgnoreCase("spawnKingdom") && sender instanceof Player){
                Player p = (Player) sender;
                Location center = p.getTargetBlock((Set<Material>) null, 90).getLocation(); // When you have to cast null to set :ok_hand:
                p.sendMessage(center.toString());
                Kingdom k = new Kingdom(center);
                k.build();
            }
            return true;
        }
    }
}
