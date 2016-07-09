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
package io.dallen.kingdoms.utilities.Utils;

import java.util.Date;

/**
 *
 * @author donoa_000
 */
public class TimeUtil {

    public static Date getDate(String d) {//only works for hours:min:sec needs to expand at some point
        String[] args = d.split(":");
        if (args.length != 3) {
            LogUtil.printDebug("Invalid Date Time format!");
            return null;
        }

        return new Date(System.currentTimeMillis()
                + (Long.parseLong(args[0]) * 1000 * 60 * 60)
                + (Long.parseLong(args[1]) * 1000 * 60)
                + (Long.parseLong(args[2]) * 1000));
    }

    public static String asTime(Date d1, Date d2) { // This seems like a really bad way of doing this, need to fix at some point
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
