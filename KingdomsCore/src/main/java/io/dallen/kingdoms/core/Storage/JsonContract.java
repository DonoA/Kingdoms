/*
 * Copyright 2016 Morphics Network.
 * 
 * This file is part of KingdomsCore for the Morphics Network.
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
package io.dallen.kingdoms.core.Storage;

import io.dallen.kingdoms.core.Contract;
import io.dallen.kingdoms.core.Structures.Vaults.BuildingVault;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonItemStack;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import io.dallen.kingdoms.utilities.Utils.DBmanager;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Donovan Allen
 */
public class JsonContract implements SaveType.NativeType.JsonType {

    @Setter
    @Getter
    private String type;

    @Getter
    @Setter
    private String contractTarget;

    @Getter
    @Setter
    private UUID contractor;

    @Getter
    @Setter
    private String rewardType;

    @Getter
    @Setter
    private JsonItemStack contractItem;

    @Getter
    @Setter
    private Object Reward;

    @Getter
    @Setter
    private HashMap<String, Object> attr = new HashMap<String, Object>();

    @Override
    public Contract toJavaObject() {
        try {
            Contract c = (Contract) Class.forName(type).newInstance();
            for (Map.Entry<String, Object> e : attr.entrySet()) {
                Object obj = e.getValue();
                if (e.getValue() instanceof LinkedHashMap && ((LinkedHashMap) e.getValue()).containsKey("type") && ((LinkedHashMap) e.getValue()).containsKey("data")) {
                    obj = DBmanager.getJSonParser().readValue(DBmanager.getJSonParser().writeValueAsString(((LinkedHashMap) e.getValue()).get("data")),
                            Class.forName((String) ((LinkedHashMap) e.getValue()).get("type")));
                }
                if (obj instanceof SaveType.NativeType && obj != null) {
                    obj = ((SaveType.NativeType) obj).toJavaObject();
                }
                Field dat = Class.forName(type).getDeclaredField(e.getKey());
                if (!dat.isAccessible()) {
                    dat.setAccessible(true);
                }
                dat.set(c, obj);
            }
        } catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException | NoSuchFieldException |
                SecurityException ex) {
            Logger.getLogger(JsonContract.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
