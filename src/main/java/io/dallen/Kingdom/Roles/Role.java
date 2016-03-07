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
package io.dallen.Kingdom.Roles;

import lombok.Getter;

/**
 *
 * @author donoa_000
 */
public abstract class Role {
    @Getter
    private String Title;
    
    @Getter
    private boolean Master;
    
    @Getter
    private int Renoun;
    
}
