/*
 * Copyright 2016 Donovan Allen.
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
package io.dallen.kingdoms.core.Storage;

import io.dallen.kingdoms.core.Municipality;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonEllipse;
import io.dallen.kingdoms.utilities.Storage.JsonClasses.JsonPolygon;
import io.dallen.kingdoms.utilities.Storage.SaveType;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonMunicipality implements SaveType.NativeType.JsonType {

    @Getter
    @Setter
    private ArrayList<Integer> Structures;

    @Getter
    @Setter
    private int Center;

    @Getter
    @Setter
    private int MunicipalID;

    @Getter
    @Setter
    private JsonWallSystem walls;

    @Getter
    @Setter
    private JsonPolygon Base;

    @Getter
    @Setter
    private JsonEllipse Influence;

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private int kingdom;

    @Getter
    @Setter
    private Date creation;

    @Override
    public Municipality toJavaObject() {
        Municipality muni = new Municipality();
        Polygon pg = (Base != null ? Base.toJavaObject() : null);
        muni.setBase(pg);
        muni.setInfluence(Influence.toJavaObject());
        muni.setType(Municipality.MunicipalType.valueOf(type));
        muni.setCreation(creation);
        return muni;
    }

}
