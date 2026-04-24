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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

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
                        BlockPos worldPos = SableUtil.toWorldPos(level.logicalPose(), p);
                        BlockPos worldController = SableUtil.toWorldPos(level.logicalPose(), c);

                        Vec3 posCenter = Vec3.atCenterOf(worldPos).subtract(cam);
                        Vec3 controllerCenter = Vec3.atCenterOf(worldController).subtract(cam);

                        double dist = Math.sqrt(p.distSqr(c));
                        float alpha = Math.max(0.1F, 1.0F - ((float) dist / 15.0F));
                        int alphaInt = (int) (alpha * 255.0F);

                        BlockState state = SableUtil.getState(level, p);
                        String id = state != null ? BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath() : "NULL";

                        new PoseUtil(pose).push().run(() -> {
                                    Matrix4f matrix = pose.last().pose();
                                    bufferSource.getBuffer(RenderType.lines()).addVertex(matrix, (float) controllerCenter.x, (float) controllerCenter.y, (float) controllerCenter.z).setColor(0, 255, 0, alphaInt).setNormal(pose.last(), 0, 1, 0);
                                    bufferSource.getBuffer(RenderType.lines()).addVertex(matrix, (float) posCenter.x, (float) posCenter.y, (float) posCenter.z).setColor(0, 255, 0, alphaInt).setNormal(pose.last(), 0, 1, 0);
                                }).pop()
                                .push().translate(posCenter.x, posCenter.y, posCenter.z).scale(-0.015F, -0.015F, 0.015F).run(() -> {
                                    pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
                                    font.drawInBatch(id, (float) -font.width(id) / 2, 0, 0xFFFFFFFF, false, pose.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
                                }).pop();

                        controllers.add(worldController);

                        bufferSource.endLastBatch();
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
                            AABB sealedBox = new AABB(p).move(-cam.x, -cam.y, -cam.z);
                            LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), sealedBox, 0.0F, 1.0F, 0.0F, 0.25F);
                            controllers.add(c);
                        });
                    }
                }
            }

            controllers.forEach(c -> LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), new AABB(c).move(-cam.x, -cam.y, -cam.z), 1.0F, 0.0F, 0.0F, 1.0F));
        });
    }
}