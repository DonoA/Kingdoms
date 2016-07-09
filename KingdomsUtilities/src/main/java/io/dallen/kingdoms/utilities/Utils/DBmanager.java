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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import lombok.Getter;

/**
 *
 * @author Donovan <dallen@dallen.xyz>
 *
 * Dallen's personal JSON database manager
 */
public class DBmanager {

    @Getter
    private static ObjectMapper JSonParser = new ObjectMapper();

    @Getter
    private static String FileSep = System.getProperty("file.separator");

    /**
     * Saves the object in JSON to the given folder and with the given name
     *
     * @param obj The object to be saved
     * @param loc the folder to save the object into
     * @param name the name of the file for the object save
     * @return returns if the file saved
     */
    public static boolean saveObj(Object obj, File loc, String name) {
        if (!loc.exists()) {
            loc.mkdirs();
        }
        boolean success = true;
        File locStart = new File(loc + FileSep + name + ".new");
        File locEnd = new File(loc + FileSep + name);
        try {
            JSonParser.writeValue(locStart, obj);
        } catch (IOException ex) {
            success = false;
        } finally {
            if (success) {
                if (locEnd.exists()) {
                    locEnd.delete();
                }
                locStart.renameTo(locEnd);
            }
        }
        return success;
    }

    /**
     * Loads the object of class type out of JSON from the given file
     *
     * @param type The class of the object to be loaded
     * @param loc the location of the file to load the data from
     * @return returns the loaded object as an Object
     */
    public static Object loadObj(Class type, File loc) {
        if (!loc.exists()) {
            loc.mkdirs();
            return false;
        }
        Object obj;
        try {
            obj = JSonParser.readValue(loc, type);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return obj;
    }

    /**
     * Loads the object of class type out of JSON from the given file
     *
     * @param type The class of the object to be loaded
     * @param path the location of the file to load the data from as a String
     * @return returns the loaded object as an Object
     */
    public static Object loadObj(Class type, String path) {
        File loc = new File(path);
        if (!loc.exists()) {
            loc.mkdirs();
            return false;
        }
        Object obj;
        try {
            obj = JSonParser.readValue(loc, type);
        } catch (Exception ex) {
//            ex.printStackTrace();
            return false;
        }
        return obj;
    }

    /**
     * Loads all the objects of class type out of JSON from the given folder
     *
     * @param type The class of the objects to be loaded
     * @param loc the location of the folder to load the data from
     * @return returns a HashMap of the file names and the Objects loaded
     */
    public static HashMap<String, Object> loadAllObj(Class type, File loc) {
        if (!loc.exists()) {
            loc.mkdirs();
            return null;
        }
        HashMap<String, Object> rtn = new HashMap<String, Object>();
        for (File f : loc.listFiles()) {
            rtn.put(f.getName(), loadObj(type, f));
        }
        return rtn;
    }
}
