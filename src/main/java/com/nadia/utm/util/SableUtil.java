package com.nadia.utm.util;

import com.nadia.utm.compat.ConstraintData;
import com.nadia.utm.compat.IConstraintAccessor;
import com.nadia.utm.compat.IContraptionNBTAccessor;
import com.nadia.utm.utm;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.KinematicContraption;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.physics.impl.rapier.RapierPhysicsPipeline;
import dev.ryanhcode.sable.platform.SableAssemblyPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachmentPoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.link_block.SwivelBearingPlateBlockEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.joml.Vector2i;
import org.joml.Vector3d;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class SableUtil {
    /**
     * get sable sublevel chunk via world (global) pos
     *
     * @param level sublevel
     * @param pos   position
     * @return chunk
     */
    public static LevelChunk getChunkWorldPos(SubLevel level, BlockPos pos) {
        LevelPlot plot = level.getPlot();
        return plot.getChunk(plot.toLocal(new ChunkPos(toLocalPos(level.logicalPose(), pos))));
    }

    /**
     * get sable sublevel chunk via sable (local) pos
     *
     * @param level sublevel
     * @param pos   position
     * @return chunk
     */
    public static LevelChunk getChunkLocalPos(SubLevel level, BlockPos pos) {
        LevelPlot plot = level.getPlot();
        return plot.getChunk(plot.toLocal(new ChunkPos(pos)));
    }

    public static BlockPos toBlockPos(Vec3 pos) {
        return BlockPos.containing(pos);
    }

    public static Vec3 toVec(BlockPos pos) {
        return Vec3.atCenterOf(pos);
    }

    /**
     * converts world (global) space pos to sable (local) space pos
     *
     * @param pose sable pose
     * @param pos  position
     * @return sable (local) space pos
     */
    public static BlockPos toLocalPos(Pose3d pose, BlockPos pos) {
        return toBlockPos(pose.transformPositionInverse(toVec(pos)));
    }

    /**
     * converts sable (local) space pos to world (global) space pos
     *
     * @param pose sable pose
     * @param pos  position
     * @return world (global) space pos
     */
    public static BlockPos toGlobalPos(Pose3d pose, BlockPos pos) {
        return toBlockPos(pose.transformPosition(toVec(pos)));
    }

    /**
     * get blockstate for a block in a sable sublevel
     *
     * @param level sable sublevel
     * @param pos   block position in sable (local) space
     * @return blockstate or null if there is no chunk
     */
    @Nullable
    public static BlockState getState(SubLevel level, BlockPos pos) {
        ChunkAccess chunk = SableUtil.getChunkLocalPos(level, pos);
        if (chunk != null) {
            return chunk.getBlockState(pos);
        }

        return null;
    }

    @Nullable
    public static SubLevel getSublevel(Entity e) {
        SubLevel level = (SubLevel) SableCompanion.INSTANCE.getTrackingOrVehicleSubLevel(e);
        if (level == null)
            level = (SubLevel) SableCompanion.INSTANCE.getContaining(e);

        return level;
    }

    public static class DimensionController {
        @SuppressWarnings("unchecked")
        @Nullable
        public static <T extends BlockEntity> T getEntityFromAllLevels(List<ServerSubLevel> levels, BlockPos pos) {
            for (ServerSubLevel level : levels) {
                LevelChunk chunk = getChunkLocalPos(level, pos);
                if (chunk == null) continue;

                BlockEntity be = chunk.getBlockEntity(pos);
                if (be != null) return (T) be;
            }

            return null;
        }

        @Nullable
        public static BlockState getStateFromAllLevels(List<ServerSubLevel> levels, BlockPos pos) {
            for (ServerSubLevel level : levels) {
                BlockState state = getState(level, pos);
                if (state != null) return state;
            }

            return null;
        }

        public static void changeDimension(ServerSubLevel originLevel, ServerLevel origin, ServerLevel target) {
            ServerSubLevelContainer oContainer = SubLevelContainer.getContainer(origin);
            ServerSubLevelContainer container = SubLevelContainer.getContainer(target);
            if (container == null || oContainer == null) return;

            SubLevelPhysicsSystem oPhysics = oContainer.physicsSystem();
            PhysicsPipeline oPipeline = oPhysics.getPipeline();
            PhysicsPipeline pipeline = container.physicsSystem().getPipeline();
            if (!(oPipeline instanceof RapierPhysicsPipeline rapier)) return;

            Set<ConstraintData<?>> allConstraints = new HashSet<>();
            Set<ServerSubLevel> processed = new HashSet<>();

            if (rapier instanceof IConstraintAccessor accessor) {
                new Consumer<ServerSubLevel>() {
                    @Override
                    public void accept(ServerSubLevel sub) {
                        if (processed.contains(sub)) return;
                        processed.add(sub);
                        var constraints = accessor.utm$getForLevel(sub);
                        for (ConstraintData<?> constraint : constraints) {
                            if (constraint.sublevelA() == constraint.sublevelB())
                                continue; // self connecting ropes n stuff will translate 1:1

                            allConstraints.add(constraint);
                            this.accept(constraint.sublevelA());
                            this.accept(constraint.sublevelB());
                        }
                    }
                }.accept(originLevel);
            }

            if (allConstraints.isEmpty()) processed.add(originLevel);

            // utility refmaps
            Map<ServerSubLevel, ServerSubLevel> levelMap = new HashMap<>();
            for (ServerSubLevel level : processed) {
                Pose3d pose = level.logicalPose();
                ServerSubLevel next = (ServerSubLevel) container.allocateNewSubLevel(pose);
                levelMap.put(level, next);
            }

            // load chunks
            Set<ChunkPos> chunks = new HashSet<>();
            for (ServerSubLevel level : processed) {
                Vector3d pos = level.logicalPose().position();
                chunks.addAll(ChunkPos.rangeClosed(new ChunkPos(BlockPos.containing(pos.x, pos.y, pos.z)), 2).toList());
            }

            for (ChunkPos pos : chunks) {
                target.getChunk(pos.x, pos.z, ChunkStatus.FULL, true);
            }

            // old be -> new be
            Map<BlockEntity, BlockEntity> beMap = new HashMap<>();

            // old net -> new net
            Map<Long, Long> networkMap = new HashMap<>();
            Set<BlockEntity> toRecalc = new HashSet<>();

            // be compat maps
            List<Pair<RopeStrandHolderBlockEntity, RopeStrandHolderBlockEntity>> ropeMap = new ArrayList<>();
            Set<RopeStrandHolderBlockEntity> ropesUsed = new HashSet<>();
            Map<SwivelBearingBlockEntity, SwivelBearingPlateBlockEntity> swivelMap = new HashMap<>();
            Map<SpringBlockEntity, SpringBlockEntity> springMap = new HashMap<>();

            final BoundingBox3i[] oldBounds = {null};
            for (ServerSubLevel level : processed) {
                List<BlockPos> blocks = new ArrayList<>();
                for (PlotChunkHolder chunkHolder : level.getPlot().getLoadedChunks()) {
                    LevelChunk chunk = chunkHolder.getChunk();
                    for (int i = 0; i < chunk.getSections().length; i++) {
                        LevelChunkSection section = chunk.getSection(i);
                        if (section.hasOnlyAir()) continue;

                        int sY = chunk.getSectionYFromSectionIndex(i);
                        ChunkPos cPos = chunk.getPos();

                        BlockPos.betweenClosedStream(0, 0, 0, 15, 15, 15).forEach(bPos   -> {
                            if (!section.getBlockState(bPos.getX(), bPos.getY(), bPos.getZ()).isAir()) {
                                BlockPos pos = SectionPos.of(cPos, sY).origin().offset(bPos);
                                blocks.add(pos);

                                if (oldBounds[0] == null)
                                    oldBounds[0] = new BoundingBox3i(pos, pos);
                                else
                                    oldBounds[0].expandTo(pos.getX(), pos.getY(), pos.getZ());
                            }
                        });
                    }
                }

                if (blocks.isEmpty()) continue;

                ServerSubLevel next = levelMap.get(level);
                BlockPos anchor = level.getPlot().getCenterBlock();
                BlockPos nextAnchor = next.getPlot().getCenterBlock();

                SubLevelAssemblyHelper.AssemblyTransform transform = new SubLevelAssemblyHelper.AssemblyTransform(
                        anchor, nextAnchor, 0, Rotation.NONE, target
                );

                next.setName(level.getName());

                SubLevelAssemblyHelper.moveOtherStuff(target, transform, blocks, oldBounds[0]);
                moveBlocksWithoutUpdate(origin, transform, blocks);
                next.getPlot().updateBoundingBox();

                for (KinematicContraption contraption : level.getPlot().getContraptions()) {
                    if (contraption instanceof AbstractContraptionEntity prev) {
                        Vec3 targetPos = transform.apply(prev.position());
                        float yRot = prev.getYRot();
                        float xRot = prev.getXRot();

                        Map<UUID, Integer> passengers = new HashMap<>(prev.getContraption().getSeatMapping());

                        RegistryFriendlyByteBuf tempBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), target.registryAccess(), ConnectionType.OTHER);
                        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), target.registryAccess(), ConnectionType.OTHER);
                        if (prev instanceof IContraptionNBTAccessor accessor)
                            accessor.utm$writeAllData(tempBuf);

                        CompoundTag tag = (CompoundTag) tempBuf.readNbt(NbtAccounter.unlimitedHeap());
                        if (tag == null) {
                            utm.LOGGER.warn("[UTM] contraption {} missing all nbt data?", prev);
                            continue;
                        }

                        CompoundTag tContraption = (CompoundTag) tag.get("Contraption");
                        if (tContraption != null) {
                            Optional<BlockPos> bp = NbtUtils.readBlockPos(tContraption, "Anchor");
                            bp.ifPresent(pos -> {
                                tContraption.put("Anchor", NbtUtils.writeBlockPos(transform.apply(pos)));

                                tag.put("Contraption", tContraption);
                            });
                        }

                        buf.writeNbt(tag);

                        AbstractContraptionEntity entity = (AbstractContraptionEntity) prev.getType().create(target);
                        prev.discard();
                        if (entity == null) continue;

                        entity.setPos(targetPos);
                        entity.setYRot(yRot);
                        entity.setXRot(xRot);
                        entity.readSpawnData(buf);

                        if (entity.getContraption() == null)
                            continue;

                        target.addFreshEntity(entity);

                        passengers.forEach((uuid, seatIndex) -> {
                            Entity pass = origin.getEntity(uuid);

                            if (pass != null)
                                pass.changeDimension(new DimensionTransition(
                                        target,
                                        transform.apply(pass.position()),
                                        pass.getDeltaMovement(),
                                        pass.getYRot(),
                                        pass.getXRot(),
                                        (newPass) -> {
                                            entity.addSittingPassenger(newPass, seatIndex);
                                            newPass.addTag("utm_reentry_landing");
                                        }
                                ));
                        });
                    }
                    level.getPlot().removeContraption(contraption);
                }

                //TODO: test literally every other be
                /*
                 * nonstandard blockentities
                 * its okay to serialize the majority of blocks BUT blocks with positional/uuid data will have trouble with the moving plot positions
                 * so. me. dont forget to mitigate that here.
                 */
                for (BlockPos oldPos : blocks) {
                    BlockPos newPos = transform.apply(oldPos);

                    BlockEntity be = target.getBlockEntity(newPos);

                    if (be != null) {
                        BlockEntity oldBE = origin.getBlockEntity(oldPos);
                        assert oldBE != null;

                        CompoundTag tag = be.saveWithFullMetadata(target.registryAccess());
                        switch (oldBE) {
                            case RopeStrandHolderBlockEntity rope -> {
                                if (rope.getBehavior().ownsRope() && !ropesUsed.contains(rope)) {
                                    ServerRopeStrand strand = rope.getBehavior().getOwnedStrand();
                                    if (strand != null) {
                                        RopeStrandHolderBlockEntity startBE = (RopeStrandHolderBlockEntity) origin.getBlockEntity(strand.getAttachment(RopeAttachmentPoint.START).blockAttachment());
                                        RopeStrandHolderBlockEntity endBE = (RopeStrandHolderBlockEntity) origin.getBlockEntity(strand.getAttachment(RopeAttachmentPoint.END).blockAttachment());

                                        if (startBE != null && endBE != null) {
                                            ropeMap.add(new Pair<>(startBE, endBE));
                                            ropesUsed.add(startBE);
                                            ropesUsed.add(endBE);
                                        }
                                    }
                                }

                                tag.remove("HasRopeAttached");
                                tag.remove("Strand");
                                tag.putBoolean("OwnStrand", false);
                            }

                            case SwivelBearingBlockEntity swivel -> {
                                if (swivel.isAssembled()) {
                                    BlockPos platePos = swivel.getPlatePos();
                                    ServerSubLevel connected = (ServerSubLevel) oContainer.getSubLevel(swivel.getSubLevelID());

                                    if (platePos != null && connected != null) {
                                        SwivelBearingPlateBlockEntity plate = (SwivelBearingPlateBlockEntity) origin.getBlockEntity(platePos);
                                        swivelMap.put(swivel, plate);
                                    }
                                }

                                tag.remove("SubLevelID");
                                tag.remove("SwivelPlate");
                            }

                            case SpringBlockEntity spring -> {
                                if (spring.isController())
                                    springMap.put(spring, spring.getPairedSpring());

                                tag.remove("GoalSubLevel");
                                tag.remove("Goal");
                            }

                            default -> {
                            }
                        }

                        if (tag.contains("Network")) {
                            long nextID = networkMap.computeIfAbsent(tag.getLong("Network"), k -> java.util.concurrent.ThreadLocalRandom.current().nextLong());
                            tag.putLong("Network", nextID);
                        }

                        utm.LOGGER.warn("[UTM] tag {}", tag);

                        be.loadWithComponents(tag, target.registryAccess());

                        toRecalc.add(be);
                        beMap.put(oldBE, be);
                    }
                }

                for (UUID uuid : level.getTrackingPlayers()) {
                    Entity pass = origin.getEntity(uuid);
                    if (pass != null)
                        pass.changeDimension(new DimensionTransition(
                                target,
                                pass.position(),
                                pass.getDeltaMovement(),
                                pass.getYRot(),
                                pass.getXRot(),
                                (newPass) -> newPass.addTag("utm_reentry_landing")
                        ));
                }

                next.getTrackingPlayers().addAll(level.getTrackingPlayers());
                next.updateBoundingBox();

                Vector3d vel = new Vector3d(), ang = new Vector3d();
                oPipeline.getLinearVelocity(level, vel);
                oPipeline.getAngularVelocity(level, ang);

                pipeline.addLinearAndAngularVelocity(next, vel, ang);

                Vector3d pos = next.logicalPose().position();
                pos.add(level.logicalPose().position().sub(next.logicalPose().position()));

                pipeline.teleport(next, pos, next.logicalPose().orientation());

                next.updateLastPose();

                SubLevelAssemblyHelper.moveTrackingPoints(target, oldBounds[0], next, transform);
            }


            List<ServerSubLevel> inLevels = levelMap.keySet().stream().toList();
            List<ServerSubLevel> outLevels = levelMap.values().stream().toList();

            for (Pair<RopeStrandHolderBlockEntity, RopeStrandHolderBlockEntity> ropes : ropeMap) {
                RopeStrandHolderBlockEntity old1 = ropes.getA(),
                        old2 = ropes.getB();
                RopeStrandHolderBlockEntity new1 = (RopeStrandHolderBlockEntity) beMap.get((BlockEntity) ropes.getA()),
                        new2 = (RopeStrandHolderBlockEntity) beMap.get((BlockEntity) ropes.getB());

                if (new1 != null & new2 != null)
                    new1.getBehavior().createRope(new2.getBehavior());
            }

            for (Map.Entry<SwivelBearingBlockEntity, SwivelBearingPlateBlockEntity> entry : swivelMap.entrySet()) {
                SwivelBearingBlockEntity swivel = entry.getKey();
                SwivelBearingPlateBlockEntity plate = entry.getValue();

                SwivelBearingBlockEntity newSwivel = (SwivelBearingBlockEntity) beMap.get(swivel);
                SwivelBearingPlateBlockEntity newPlate = (SwivelBearingPlateBlockEntity) beMap.get(plate);

                if (newSwivel != null && newPlate != null) {
                    ServerSubLevel targetLevel = (ServerSubLevel) SableCompanion.INSTANCE.getContaining(newPlate);
                    if (targetLevel == null) continue;

                    newSwivel.setSubLevelID(targetLevel.getUniqueId());
                    newSwivel.setPlatePos(newPlate.getBlockPos());
                    newSwivel.reattachConstraint(targetLevel, true);
                }
            }

            for (Map.Entry<SpringBlockEntity, SpringBlockEntity> entry : springMap.entrySet()) {
                SpringBlockEntity old1 = entry.getKey(), old2 = entry.getValue();
                SpringBlockEntity new1 = (SpringBlockEntity) beMap.get(old1), new2 = (SpringBlockEntity) beMap.get(old2);

                if (new1 != null && new2 != null) {
                    ServerSubLevel partnerLevel = (ServerSubLevel) SableCompanion.INSTANCE.getContaining(new2);
                    if (partnerLevel == null) continue;

                    new1.setPartnerPos(new2.getBlockPos(), partnerLevel.getUniqueId());
                }
            }

            for (BlockEntity be : toRecalc)
                if (be instanceof KineticBlockEntity kbe) kbe.attachKinetics();

            for (ServerSubLevel lev : inLevels) lev.markRemoved();
        }

        @SuppressWarnings("UnstableApiUsage")
        public static void moveBlocksWithoutUpdate(final ServerLevel level, final SubLevelAssemblyHelper.AssemblyTransform transform, final Iterable<BlockPos> blocks) {
            final ServerLevel resultingLevel = transform.getLevel();

            BlockPos firstBlock = null;
            Vector2i chunkBoundsMin = null;
            Vector2i chunkBoundsMax = null;
            for (final BlockPos block : blocks) {
                if (firstBlock == null)
                    firstBlock = block;

                final ChunkPos chunk = new ChunkPos(transform.apply(block));
                final Vector2i jomlChunkPos = new Vector2i(chunk.x, chunk.z);
                if (chunkBoundsMin == null) {
                    chunkBoundsMin = new Vector2i(jomlChunkPos);
                    chunkBoundsMax = new Vector2i(jomlChunkPos);
                }

                chunkBoundsMin.min(jomlChunkPos);
                chunkBoundsMax.max(jomlChunkPos);
            }

            final SubLevel subLevel = Sable.HELPER.getContaining(resultingLevel, transform.apply(firstBlock));
            if (subLevel != null) {
                final LevelPlot plot = subLevel.getPlot();

                assert chunkBoundsMin != null;
                for (int chunkX = chunkBoundsMin.x; chunkX <= chunkBoundsMax.x; chunkX++)
                    for (int chunkZ = chunkBoundsMin.y; chunkZ <= chunkBoundsMax.y; chunkZ++) {
                        ChunkPos pos = new ChunkPos(chunkX, chunkZ);

                        if (plot.getChunkHolder(plot.toLocal(pos)) == null)
                            plot.newEmptyChunk(pos);
                    }
            }

            SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace(resultingLevel, true);
            final List<BlockState> states = new ArrayList<>();
            for (final BlockPos untransformed : blocks) {
                final BlockState rotatedState = level.getBlockState(untransformed);
                final BlockPos transformed = transform.apply(untransformed);

                try {
                    final BlockState subLevelState = transform.apply(rotatedState);
                    final LevelChunk chunk = (LevelChunk) resultingLevel.getChunk(SectionPos.blockToSectionCoord(transformed.getX()), SectionPos.blockToSectionCoord(transformed.getZ()), ChunkStatus.FULL, true);

                    assert chunk != null;
                    chunk.setBlockState(transformed, subLevelState, true);
                    states.add(subLevelState);
                } catch (final Exception e) {
                    utm.LOGGER.error("[UTM] failed to move block {} {} {} {}", rotatedState, untransformed, transformed, e);
                }
            }
            SableAssemblyPlatform.INSTANCE.setIgnoreOnPlace(resultingLevel, false);

            int index = 0;
            for (final BlockPos untransformed : blocks) {
                final BlockPos transformed = transform.apply(untransformed);

                try {
                    final LevelChunk chunk = (LevelChunk) resultingLevel.getChunk(SectionPos.blockToSectionCoord(transformed.getX()), SectionPos.blockToSectionCoord(transformed.getZ()), ChunkStatus.FULL, true);
                    final BlockState state = states.get(index);
                    markAndNotifyBlock(resultingLevel, transformed, chunk, Blocks.AIR.defaultBlockState(), state, 3, 512);
                } catch (final Exception e) {
                    utm.LOGGER.error("[UTM] failed to mark block {} {} {})", transformed, untransformed, e);
                }

                index++;
            }
        }

        public static void markAndNotifyBlock(final Level resultingLevel, final BlockPos pos, final LevelChunk chunk, final BlockState oldState, final BlockState newState, final int flags, final int recursionLeft) {
            final Block block = newState.getBlock();
            final BlockState worldState = resultingLevel.getBlockState(pos);
            if (worldState == newState) {
                if (oldState != worldState)
                    resultingLevel.setBlocksDirty(pos, oldState, worldState);

                if ((flags & 2) != 0 && chunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))
                    resultingLevel.sendBlockUpdated(pos, oldState, newState, flags);

                if ((flags & 1) != 0) {
                    resultingLevel.blockUpdated(pos, oldState.getBlock());
                    if (newState.hasAnalogOutputSignal()) {
                        resultingLevel.updateNeighbourForOutputSignal(pos, block);
                    }
                }

                if ((flags & 16) == 0 && recursionLeft > 0) {
                    final int i = flags & -34;
                    oldState.updateIndirectNeighbourShapes(resultingLevel, pos, i, recursionLeft - 1);
                    newState.updateNeighbourShapes(resultingLevel, pos, i, recursionLeft - 1);
                    newState.updateIndirectNeighbourShapes(resultingLevel, pos, i, recursionLeft - 1);
                }

                resultingLevel.onBlockStateChange(pos, oldState, worldState);
            }
        }
    }
}
