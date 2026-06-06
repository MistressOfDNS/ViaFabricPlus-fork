/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.base.integration;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.injection.access.base.IServerInfo;
import com.viaversion.viafabricplus.save.impl.SettingsSave;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerInfo.class)
public abstract class MixinServerInfo implements IServerInfo {

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

    @Inject(method = "toNbt", at = @At("TAIL"))
    private void saveForcedVersion(CallbackInfoReturnable<NbtCompound> cir, @Local NbtCompound nbtCompound) {
        if (viaFabricPlus$forcedVersion != null) {
            nbtCompound.putString("viafabricplus_forcedversion", viaFabricPlus$forcedVersion.getName());
        }
        if (viaFabricPlus$excludedFromViaFabricPlus) {
            nbtCompound.putBoolean("viafabricplus_excluded", true);
        }
    }

    @Inject(method = "fromNbt", at = @At("TAIL"))
    private static void loadForcedVersion(NbtCompound root, CallbackInfoReturnable<ServerInfo> cir, @Local ServerInfo serverInfo) {
        final IServerInfo mixinServerInfo = (IServerInfo) serverInfo;
        if (root.contains("viafabricplus_forcedversion")) {
            final ProtocolVersion version = SettingsSave.protocolVersionByName(root.getString("viafabricplus_forcedversion", null));
            if (version != null) {
                mixinServerInfo.viaFabricPlus$forceVersion(version);
            }
        }
        mixinServerInfo.viaFabricPlus$excludeFromViaFabricPlus(root.contains("viafabricplus_excluded") && root.getBoolean("viafabricplus_excluded"));
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void syncForcedVersion(ServerInfo serverInfo, CallbackInfo ci) {
        final IServerInfo mixinServerInfo = (IServerInfo) serverInfo;
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
