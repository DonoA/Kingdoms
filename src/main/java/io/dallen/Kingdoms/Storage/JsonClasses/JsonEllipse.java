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
package io.dallen.Kingdoms.Storage.JsonClasses;

import io.dallen.Kingdoms.Storage.PlayerData;
import io.dallen.Kingdoms.Storage.SaveTypes;
import java.awt.geom.Ellipse2D;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonEllipse implements SaveTypes.JsonType.NativeType{
    @Getter
    private double length;
    @Getter
    private double width;
    
    @Override
    public Ellipse2D toJavaObject(){
        throw new UnsupportedOperationException("Not supported for LeatherArmorMeta");
    }
    
}
