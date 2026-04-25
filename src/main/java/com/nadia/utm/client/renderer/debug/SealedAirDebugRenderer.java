package com.nadia.utm.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.Config;
import com.nadia.utm.behavior.space.SealedChunkData;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.networking.payloads.debug.RequestSealedDataPayload;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.util.PoseUtil;
import com.nadia.utm.util.SableUtil;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ForceLoad(dist = Dist.CLIENT)
public class SealedAirDebugRenderer {
    private static final Map<LevelChunk, Long> LAST_CHECKED = new HashMap<>();
    private static final long REFRESH_RATE = 20L;

    static {
        utmEvents.register(RenderLevelStageEvent.class, event -> {
            if (!Config.DEBUG_SEALED_AIR.getAsBoolean() || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
                return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            PoseStack pose = event.getPoseStack();
            Vec3 cam = event.getCamera().getPosition();
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

            Font font = mc.font;

            Set<BlockPos> controllers = new HashSet<>();
            long tick = mc.player.level().getGameTime();

            if (SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(mc.player) instanceof SubLevel level) {
                level.getPlot().getLoadedChunks().forEach(chunk -> {
                    long lastTick = LAST_CHECKED.getOrDefault(chunk.getChunk(), -1L);
                    if (lastTick == -1L || tick - lastTick >= REFRESH_RATE) {
                        LAST_CHECKED.put(chunk.getChunk(), tick);
                        PacketDistributor.sendToServer(new RequestSealedDataPayload(chunk.getPos()));
                    }

                    chunk.getChunk().getData(utmAttachments.SEALED_AIR).sealedBlocks().forEach((p, c) -> {
                        new PoseUtil(pose).push().run(() -> LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), new AABB(SableUtil.toGlobalPos(level.logicalPose(), p)).move(-cam.x, -cam.y, -cam.z).inflate(-0.25), 0.0F, 1.0F, 0.0F, 0.35F)).pop();

                        controllers.add(SableUtil.toGlobalPos(level.logicalPose(), c));
                    });
                });
            } else {
                int cx = mc.player.chunkPosition().x;
                int cz = mc.player.chunkPosition().z;

                for (int x = cx - 2; x <= cx + 2; x++) {
                    for (int z = cz - 2; z <= cz + 2; z++) {
                        LevelChunk chunk = mc.level.getChunk(x, z);

                        long lastTick = LAST_CHECKED.getOrDefault(chunk, -1L);
                        if (lastTick == -1L || tick - lastTick >= REFRESH_RATE) {
                            LAST_CHECKED.put(chunk, tick);
                            PacketDistributor.sendToServer(new RequestSealedDataPayload(chunk.getPos()));
                        }

                        if (!chunk.hasData(utmAttachments.SEALED_AIR)) continue;

                        SealedChunkData data = chunk.getData(utmAttachments.SEALED_AIR);

                        data.sealedBlocks().forEach((p, c) -> {
                            LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), new AABB(p).move(-cam.x, -cam.y, -cam.z), 0.0F, 1.0F, 0.0F, 0.25F);
                            controllers.add(c);
                        });
                    }
                }
            }

            controllers.forEach(c -> LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), new AABB(c).move(-cam.x, -cam.y, -cam.z), 1.0F, 0.0F, 0.0F, 1.0F));
        });
    }
}