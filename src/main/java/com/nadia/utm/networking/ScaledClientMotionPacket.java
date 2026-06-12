package com.nadia.utm.networking;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;

public interface ScaledClientMotionPacket {
    float utm$getScale();

    static ScaledClientMotionPacket get(ClientboundSetEntityMotionPacket packet) {
        return (ScaledClientMotionPacket) packet;
    }
}
