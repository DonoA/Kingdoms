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
package io.dallen.Kingdoms.Util;

import lombok.Getter;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author donoa_000
 */
public class PermissionManager {
    
    @Getter
    private static final Permission ownerPermission = new Permission("kingdom.owner", PermissionDefault.OP);
    
    @Getter
    private static final Permission staffPermission = new Permission("kingdom.staff", PermissionDefault.OP);
    
    @Getter
    private static final Permission modPermission = new Permission("kingdom.mod", PermissionDefault.OP);
    
    @Getter
    private static final Permission buildPermission = new Permission("kingdom.build", PermissionDefault.OP);
    
}
