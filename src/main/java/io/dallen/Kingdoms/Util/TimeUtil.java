/*
 * This file is part of Kingdoms.
 * 
 * Kingdoms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Kingdoms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Kingdoms.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 */
package io.dallen.Kingdoms.Util;

import java.util.Date;

/**
 *
 * @author donoa_000
 */
public class TimeUtil {
    public static Date getDate(String d){//only works for hours:min:sec needs to expand at some point
        String[] args = d.split(":");
        if(args.length!=3){
            LogUtil.printDebug("Invalid Date Time format!");
            return null;
        }
        
        return new Date(System.currentTimeMillis() + 
                       (Long.parseLong(args[0])*1000*60*60) +  
                       (Long.parseLong(args[1])*1000*60) + 
                       (Long.parseLong(args[2])*1000));
    }
    
    public static String asTime(Date d1, Date d2){ // This seems like a really bad way of doing this, need to fix at some point
        long Time = d2.getTime() - d1.getTime();
        Time /= 60000;
        long m = Time % 60;
        Time /= 60;
        long h = Time % 24;
        Time /= 24;
        long d = Time;
        return (d > 0 ? String.valueOf(d) + " days, " : "") + (h > 0 ? String.valueOf(h) + " hours " : "") + (m > 0 ? String.valueOf(m) + " minutes" : "");
    }
    
}
