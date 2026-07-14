package com.nadia.utm.mixin;

import com.mojang.authlib.GameProfile;
import com.nadia.utm.config.utmServerConfig;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLoginPacketListenerImpl.class, remap = false)
public abstract class ServerLoginPacketListenerImplMixin {
    @Shadow
    abstract void startClientVerification(GameProfile profile);

    @Inject(
            method = "handleHello",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;usesAuthentication()Z"),
            cancellable = true
    )
    private void utm$canJoin(ServerboundHelloPacket packet, CallbackInfo ci) {
        String userName = packet.name();

        if (utmServerConfig.CRACKED_USERNAMES.get().contains(userName)) {
            this.startClientVerification(UUIDUtil.createOfflineProfile(userName));
            ci.cancel();
        }
    }
}
