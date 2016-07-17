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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 *
 * @author Donovan
 */
public class LogUtil {

    private static boolean isDebug = true;

    public static void printDebug(Object msg) {
        if (isDebug) {
            Bukkit.getLogger().log(Level.INFO, "[{0}][DEBUG] {1}", new Object[]{getPluginName(), msg.toString()});
        }
    }

    public static void printDebugJson(Object obj) {
        try {
            printDebug(DBmanager.getJSonParser().writeValueAsString(obj));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(LogUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void printDebugStack(Exception ex) {
        if (isDebug) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            Bukkit.getLogger().log(Level.INFO, "[{0}][DEBUG][ERROR] {1}", new Object[]{getPluginName(), sw.toString()});
        }
    }

    public static void printInfo(Object msg) {
        Bukkit.getLogger().log(Level.INFO, "[{0}] {1}", new Object[]{getPluginName(), msg.toString()});
    }

    public static void printErr(Object msg) {
        Bukkit.getLogger().log(Level.SEVERE, "[{0}] [ERROR] {1}", new Object[]{getPluginName(), msg.toString()});
    }

    public static String getPluginName() {
        String[] pkgs = Thread.currentThread().getStackTrace()[3].getClassName().split("\\.");
        return cap(pkgs[2]) + cap(pkgs[3]);
    }

    private static String cap(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
