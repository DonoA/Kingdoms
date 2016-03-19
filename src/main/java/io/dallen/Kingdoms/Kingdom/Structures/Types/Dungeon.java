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

/**
 * Allows the kingdom to imprison those that deserve it
 * 
 * @author donoa_000
 */
public class Dungeon extends Plot{

    private int capacity;
    
    private int security;
    
    private int guards;
    
    private int cells;
    
    private int currentInmates;
    
    private int currentGuards;
    
    public Dungeon(Plot p) {
        super(p.getBase(), p.getCenter(), p.getOwner(), p.getMunicipal());
    }

}
