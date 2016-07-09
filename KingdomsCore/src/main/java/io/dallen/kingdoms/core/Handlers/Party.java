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
package io.dallen.kingdoms.core.Handlers;

import io.dallen.kingdoms.core.PlayerData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author donoa_000
 */
public class Party {

    @Getter
    private static HashMap<String, Party> Parties = new HashMap<String, Party>();

    @Getter
    private Player Owner;

    @Getter
    private ArrayList<Player> Members = new ArrayList<Player>();

    @Getter
    private HashMap<Player, Date> Invites = new HashMap<Player, Date>();

    public Party(Player invite, Player owner) {
        Owner = owner;
        Invites.put(invite, new Date());
    }

    public boolean join(Player p) {
        if (Invites.containsKey(p)) {
            if (Invites.get(p).after(new Date(System.currentTimeMillis() - (120000)))) { //less than 2 min ago
                Invites.remove(p);
                Members.add(p);
                return true;
            } else {
                Invites.remove(p);
            }
        }
        return false;
    }

    public static class PartyCommands implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                PlayerData pd = PlayerData.getData(p);
                if (pd != null) {
                    if (args.length == 0) {
                        p.sendMessage("Current Party Members");
                        p.sendMessage("=====================");
                        if (pd.getCurrParty() != null) {
                            for (Player m : pd.getCurrParty().getMembers()) {
                                p.sendMessage(" - " + m.getName());
                            }
                        } else {
                            p.sendMessage("Your Party is currently empty");
                        }
                    } else {
                        if (args[0].equalsIgnoreCase("leave") && pd.getCurrParty() != null) {
                            for (Player m : pd.getCurrParty().getMembers()) {
                                m.sendMessage(p.getName() + " has left your party");
                            }
                            p.sendMessage("You have left the party");
                            pd.setPartyID(null);
                        } else if (args[0].equalsIgnoreCase("join") && args.length > 1) {
                            PlayerData joinDat;
                            if (Bukkit.getPlayer(args[1]) != null && (joinDat = PlayerData.getData(Bukkit.getPlayer(args[1]))) != null) {
                                if (joinDat.getCurrParty() != null && joinDat.getCurrParty().join(p)) {
                                    p.sendMessage("Joined " + args[1] + "'s party");
                                } else {
                                    p.sendMessage("You do not have a current invite to that party!");
                                }
                            } else {
                                p.sendMessage("That player is not online!");
                            }
                        } else if (args[0].equalsIgnoreCase("invite") && args.length > 1) {
                            Player r;
                            if ((r = Bukkit.getPlayer(args[1])) != null) {
                                if (pd.getCurrParty() == null) {
                                    Parties.put(p.getName(), new Party(r, p));
                                    pd.setPartyID(p.getName());
                                } else {
                                    pd.getCurrParty().getInvites().put(r, new Date());
                                }
                                r.sendMessage(p.getName() + " has invited you to a party");
                                r.sendMessage("Type /party join " + p.getName());
                            }
                        } else if (args[0].equalsIgnoreCase("kick") && args.length > 1) {
                            Player r;
                            if ((r = Bukkit.getPlayer(args[1])) != null && pd.getCurrParty() != null
                                    && pd.getCurrParty().getOwner().equals(p)) {
                                pd.getCurrParty().getMembers().remove(r);
                            }
                        }
                    }
                } else {
                    p.sendMessage("[Parties] No Player Data found...");
                }
            } else {
                sender.sendMessage("[Parties] Only players can be in parties");
            }
            return true;
        }
    }
}
