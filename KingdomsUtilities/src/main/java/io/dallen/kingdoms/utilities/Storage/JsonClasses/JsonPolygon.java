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
package io.dallen.kingdoms.utilities.Storage.JsonClasses;

import io.dallen.kingdoms.utilities.Storage.SaveType;
import java.awt.Polygon;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Donovan Allen
 */
@NoArgsConstructor
public class JsonPolygon implements SaveType.NativeType{
    
    @Getter @Setter
    private int[] Xs;
    
    @Getter @Setter
    private int[] Ys;
    
    public JsonPolygon(Polygon Base){
        this.Xs = Base.xpoints;
        this.Ys = Base.ypoints;
    }
    
    @Override
    public Polygon toJavaObject(){
        return new Polygon(Xs, Ys, Xs.length);
    }
    
}
