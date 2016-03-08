/*
 * This file is part of Kingdoms.
 * 
 * EnforcerSuite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EnforcerSuite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with EnforcerSuite.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms.Util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Donovan
 */
public class LogUtil {
    
    private static boolean isDebug = true;
    
    public static void printDebug(Object msg){
        if(isDebug)
            Bukkit.getLogger().log(Level.INFO, "[Kingdoms] [DEBUG] {0}", msg.toString());
    }
    
    public static void printDebugStack(Exception ex){
        if(isDebug){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            Bukkit.getLogger().log(Level.INFO, "[Kingdoms] [DEBUG] [ERROR] {0}", sw.toString());
        }
    }
    
    public static void printInfo(Object msg){
        Bukkit.getLogger().log(Level.INFO, "[Kingdoms] {0}", msg.toString());
    }
    
    public static void printErr(Object msg){
        Bukkit.getLogger().log(Level.SEVERE, "[Kingdoms]{0} [ERROR] {1}", new Object[]{ChatColor.RED, msg.toString()});
    }
}
