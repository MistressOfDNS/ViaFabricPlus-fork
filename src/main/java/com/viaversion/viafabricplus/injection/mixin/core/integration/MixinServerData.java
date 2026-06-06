/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.injection.mixin.core.integration;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.injection.access.core.IServerData;
import com.viaversion.viafabricplus.save.impl.SettingsSave;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerData.class)
public abstract class MixinServerData implements IServerData {

    @Shadow
    public String name;

    @Unique
    private ProtocolVersion viaFabricPlus$forcedVersion = null;

    @Unique
    private boolean viaFabricPlus$excludedFromViaFabricPlus;

    @Unique
    private boolean viaFabricPlus$passedDirectConnectScreen;

    @Unique
    private ProtocolVersion viaFabricPlus$translatingVersion;

    @Inject(method = "write", at = @At("TAIL"))
    private void saveForcedVersion(CallbackInfoReturnable<CompoundTag> cir, @Local CompoundTag nbtCompound) {
        if (viaFabricPlus$forcedVersion != null) {
            nbtCompound.putString("viafabricplus_forcedversion", viaFabricPlus$forcedVersion.getName());
        }
        if (viaFabricPlus$excludedFromViaFabricPlus) {
            nbtCompound.putBoolean("viafabricplus_excluded", true);
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private static void loadForcedVersion(CompoundTag root, CallbackInfoReturnable<ServerData> cir, @Local ServerData serverInfo) {
        final IServerData mixinServerInfo = (IServerData) serverInfo;
        if (root.contains("viafabricplus_forcedversion")) {
            final ProtocolVersion version = SettingsSave.protocolVersionByName(root.getStringOr("viafabricplus_forcedversion", null));
            if (version != null) {
                mixinServerInfo.viaFabricPlus$forceVersion(version);
            }
        }
        mixinServerInfo.viaFabricPlus$excludeFromViaFabricPlus(root.getBooleanOr("viafabricplus_excluded", false));
    }

    @Inject(method = "copyNameIconFrom", at = @At("RETURN"))
    private void syncForcedVersion(ServerData serverInfo, CallbackInfo ci) {
        final IServerData mixinServerInfo = (IServerData) serverInfo;
        viaFabricPlus$forceVersion(mixinServerInfo.viaFabricPlus$forcedVersion());
        viaFabricPlus$excludeFromViaFabricPlus(mixinServerInfo.viaFabricPlus$excludedFromViaFabricPlus());
    }

    @Override
    public ProtocolVersion viaFabricPlus$forcedVersion() {
        return viaFabricPlus$forcedVersion;
    }

    @Override
    public void viaFabricPlus$forceVersion(ProtocolVersion version) {
        viaFabricPlus$forcedVersion = version;
    }

    @Override
    public boolean viaFabricPlus$excludedFromViaFabricPlus() {
        return viaFabricPlus$excludedFromViaFabricPlus;
    }

    @Override
    public void viaFabricPlus$excludeFromViaFabricPlus(boolean excluded) {
        viaFabricPlus$excludedFromViaFabricPlus = excluded;
    }

    @Override
    public boolean viaFabricPlus$passedDirectConnectScreen() {
        return viaFabricPlus$passedDirectConnectScreen;
    }

    @Override
    public void viaFabricPlus$passDirectConnectScreen(boolean state) {
        viaFabricPlus$passedDirectConnectScreen = state;
    }

    @Override
    public ProtocolVersion viaFabricPlus$translatingVersion() {
        return viaFabricPlus$translatingVersion;
    }

    @Override
    public void viaFabricPlus$setTranslatingVersion(ProtocolVersion version) {
        viaFabricPlus$translatingVersion = version;
    }

}
