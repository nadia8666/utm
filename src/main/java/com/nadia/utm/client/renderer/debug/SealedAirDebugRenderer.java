package com.nadia.utm.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.nadia.utm.Config;
import com.nadia.utm.behavior.space.SealedChunkData;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.networking.payloads.debug.RequestSealedDataPayload;
import com.nadia.utm.registry.attachment.utmAttachments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

@ForceLoad(dist = Dist.CLIENT)
public class SealedAirDebugRenderer {
    private static final Map<LevelChunk, Long> LAST_CHECKED = new HashMap<>();

    static {
        utmEvents.register(RenderLevelStageEvent.class, event -> {
            if (!Config.DEBUG_SEALED_AIR.get() || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            PoseStack pose = event.getPoseStack();
            Vec3 cam = event.getCamera().getPosition();
            VertexConsumer consumer = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());

            int cx = mc.player.chunkPosition().x;
            int cz = mc.player.chunkPosition().z;

            for (int x = cx - 2; x <= cx + 2; x++) {
                for (int z = cz - 2; z <= cz + 2; z++) {
                    LevelChunk chunk = mc.level.getChunk(x, z);

                    long tick = mc.player.level().getGameTime();
                    long lastTick = LAST_CHECKED.getOrDefault(chunk, -1L);
                    if (lastTick == -1L || tick - lastTick >= 200) {
                        LAST_CHECKED.put(chunk, tick);
                        PacketDistributor.sendToServer(new RequestSealedDataPayload(chunk.getPos()));
                    }

                    if (!chunk.hasData(utmAttachments.SEALED_AIR)) continue;

                    SealedChunkData data = chunk.getData(utmAttachments.SEALED_AIR);

                    for (Map.Entry<BlockPos, BlockPos> entry : data.sealedBlocks().entrySet()) {
                        AABB sealedBox = new AABB(entry.getKey()).move(-cam.x, -cam.y, -cam.z);
                        LevelRenderer.renderLineBox(pose, consumer, sealedBox, 0.0F, 1.0F, 0.0F, 0.25F);

                        AABB controllerBox = new AABB(entry.getValue()).move(-cam.x, -cam.y, -cam.z);
                        LevelRenderer.renderLineBox(pose, consumer, controllerBox, 1.0F, 0.0F, 0.0F, 1.0F);
                    }
                }
            }
        });
    }
}