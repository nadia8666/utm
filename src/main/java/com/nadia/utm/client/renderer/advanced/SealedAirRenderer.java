package com.nadia.utm.client.renderer.advanced;

import com.mojang.blaze3d.vertex.PoseStack;
import com.nadia.utm.Config;
import com.nadia.utm.behavior.space.SealedChunkData;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.item.AdvancedGogglesItem;
import com.nadia.utm.networking.payloads.debug.RequestSealedDataPayload;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.util.PoseUtil;
import com.nadia.utm.util.SableUtil;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
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
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ForceLoad(dist = Dist.CLIENT)
public class SealedAirRenderer {
    private static final Map<LevelChunk, Long> LAST_CHECKED = new HashMap<>();
    private static final long REFRESH_RATE = 20L;

    public static boolean shouldRender() {
        if (Config.DEBUG_SEALED_AIR.getAsBoolean())
            return true;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;

        return AdvancedGogglesItem.isWearingAdvancedGoggles(mc.player) && mc.player.isShiftKeyDown();
    }

    static {
        utmEvents.register(RenderLevelStageEvent.class, event -> {
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || !shouldRender())
                return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            PoseStack pose = event.getPoseStack();
            Vec3 cam = event.getCamera().getPosition();
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

            Set<BlockPos> controllers = new HashSet<>();
            long tick = mc.player.level().getGameTime();

            if (SableUtil.getSublevel(mc.player) instanceof SubLevel level) {
                Map<BlockPos, Boolean> vControllers = new HashMap<>();

                level.getPlot().getLoadedChunks().forEach(chunk -> {
                    long lastTick = LAST_CHECKED.getOrDefault(chunk.getChunk(), -1L);
                    if (lastTick == -1L || tick - lastTick >= REFRESH_RATE) {
                        LAST_CHECKED.put(chunk.getChunk(), tick);
                        PacketDistributor.sendToServer(new RequestSealedDataPayload(chunk.getPos()));
                    }

                    chunk.getChunk().getData(utmAttachments.SEALED_AIR).sealedBlocks().forEach((p, c) -> {
                        new PoseUtil(pose).push().run(() -> {
                            if (level instanceof ClientSubLevel cl) {
                                Vec3 pos = cl.renderPose().transformPosition(SableUtil.toVec(p)).subtract(cam);
                                pose.translate(pos.x, pos.y, pos.z);
                                pose.mulPose(new Quaternionf(cl.renderPose().orientation()));

                                double dist = SableCompanion.INSTANCE.distanceSquaredWithSubLevels(mc.player.level(), mc.player.position().x, mc.player.position().y, mc.player.position().z, p.getX(), p.getY(), p.getZ());
                                double alpha = Math.min(dist, 10) / 10;

                                LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5).inflate(-(Math.clamp(dist / 50, 0, .45))), 0.5F, .9F, 1.0F, (float) alpha * .2F);
                            }
                        }).pop();

                        vControllers.computeIfAbsent(c, (y) -> {
                            new PoseUtil(pose).push().run(() -> {
                                if (level instanceof ClientSubLevel cl) {
                                    Vec3 pos = cl.renderPose().transformPosition(SableUtil.toVec(c)).subtract(cam);
                                    pose.translate(pos.x, pos.y, pos.z);
                                    pose.mulPose(new Quaternionf(cl.renderPose().orientation()));

                                    LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5), 1.0F, 0.0F, 0.0F, 0.4F);
                                }
                            }).pop();

                            return true;
                        });
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
                            double dist = SableCompanion.INSTANCE.distanceSquaredWithSubLevels(mc.player.level(), mc.player.position().x, mc.player.position().y, mc.player.position().z, p.getX(), p.getY(), p.getZ());
                            double alpha = Math.min(dist, 10) / 10;
                            LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), new AABB(p).move(-cam.x, -cam.y, -cam.z).inflate(-(Math.clamp(dist / 50, 0, .45))), 0.5F, .9F, 1.0F, (float) alpha * .2F);
                            controllers.add(c);
                        });
                    }
                }
            }

            controllers.forEach(c -> LevelRenderer.renderLineBox(pose, bufferSource.getBuffer(RenderType.lines()), new AABB(c).move(-cam.x, -cam.y, -cam.z), 1.0F, 0.0F, 0.0F, 0.4F));
        });
    }
}