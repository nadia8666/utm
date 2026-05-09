package com.nadia.utm.block.misc.gimbal;

import com.nadia.utm.block.base.RotatableBlock;
import com.nadia.utm.compat.BlockEntitySubLevelActorExtensions;
import com.nadia.utm.registry.block.utmBlockEntities;
import com.nadia.utm.registry.block.utmBlocks;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.physics.constraint.ConstraintJointAxis;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintConfiguration;
import dev.ryanhcode.sable.api.physics.constraint.free.FreeConstraintHandle;
import dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.util.SimAssemblyHelper;
import dev.simulated_team.simulated.util.SimLevelUtil;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GimbalBlockEntity extends SmartBlockEntity implements BlockEntitySubLevelActorExtensions<GimbalBlockEntity>, IHaveGoggleInformation {
    public int ANGLE_Y = 0;
    public int ANGLE_Z = 0;
    public boolean ASSEMBLE_NEXT_TICK;
    protected AssemblyException LAST_EXCEPTION;
    public boolean ASSEMBLED = false;
    public boolean ASSEMBLING = false;
    @Nullable
    public UUID ATTACHED_LEVEL;
    @Nullable
    public BlockPos PLATE_POS;
    @Nullable
    public FreeConstraintHandle HANDLE;

    public GimbalBlockEntity(BlockPos pos, BlockState state) {
        super(utmBlockEntities.GIMBAL.get(), pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> list) {

    }

    @Override
    public @Nullable Iterable<@NotNull SubLevel> sable$getConnectionDependencies() {
        final SubLevelContainer container = SubLevelContainer.getContainer(this.level);
        final SubLevel attachedSubLevel;
        if (ATTACHED_LEVEL != null) {
            assert container != null;
            attachedSubLevel = container.getSubLevel(ATTACHED_LEVEL);
        } else
            attachedSubLevel = null;

        if (attachedSubLevel == null)
            return null;

        return List.of(attachedSubLevel);
    }

    public void tick() {
        super.tick();

        if (level == null || level.isClientSide()) return;

        if (this.ASSEMBLE_NEXT_TICK) {
            if (!ASSEMBLED) {
                this.assemble();
            } else {
                this.disassemble();
            }
        }

        ASSEMBLE_NEXT_TICK = false;

        if (ATTACHED_LEVEL != null)
            this.checkPersistence(ATTACHED_LEVEL);

        if (ASSEMBLED && HANDLE != null) {
            Direction facing = this.getBlockState().getValue(GimbalBlock.FACING);
            Direction zNDir = Direction.NORTH, zPDir = Direction.SOUTH, xPDir = Direction.EAST, xNDir = Direction.WEST;

            ConstraintJointAxis Axis1 = ConstraintJointAxis.ANGULAR_X;
            ConstraintJointAxis Axis2 = ConstraintJointAxis.ANGULAR_Z;
            int mul1 = 1;
            int mul2 = 1;

            switch (facing) {
                case DOWN -> {
                    zNDir = Direction.SOUTH;
                    zPDir = Direction.NORTH;

                    Axis1 = ConstraintJointAxis.ANGULAR_Z;
                    Axis2 = ConstraintJointAxis.ANGULAR_X;
                }
                case NORTH -> {
                    zNDir = Direction.UP;
                    zPDir = Direction.DOWN;

                    Axis1 = ConstraintJointAxis.ANGULAR_Y;
                    Axis2 = ConstraintJointAxis.ANGULAR_X;

                    mul1 = -1;
                    mul2 = -1;
                }
                case EAST -> {
                    zNDir = Direction.UP;
                    zPDir = Direction.DOWN;
                    xPDir = Direction.SOUTH;
                    xNDir = Direction.NORTH;

                    Axis1 = ConstraintJointAxis.ANGULAR_Y;

                    mul1 = -1;
                    mul2 = -1;
                }
                case SOUTH -> {
                    zNDir = Direction.UP;
                    zPDir = Direction.DOWN;
                    xPDir = Direction.WEST;
                    xNDir = Direction.EAST;

                    Axis1 = ConstraintJointAxis.ANGULAR_Y;
                    Axis2 = ConstraintJointAxis.ANGULAR_X;

                    mul1 = -1;
                }
                case WEST -> {
                    zNDir = Direction.UP;
                    zPDir = Direction.DOWN;
                    xPDir = Direction.NORTH;
                    xNDir = Direction.SOUTH;

                    Axis1 = ConstraintJointAxis.ANGULAR_Y;
                    mul1 = -1;
                }
                case UP -> {
                    Axis1 = ConstraintJointAxis.ANGULAR_Z;
                    Axis2 = ConstraintJointAxis.ANGULAR_X;

                    mul1 = -1;
                }
            }

            BlockPos pos = this.getBlockPos();


            float xPPower = this.level.getSignal(pos.relative(xPDir), xPDir) / 15.0f;
            float xNPower = this.level.getSignal(pos.relative(xNDir), xNDir) / 15.0f;
            float zPPower = this.level.getSignal(pos.relative(zPDir), zPDir) / 15.0f;
            float zNPower = this.level.getSignal(pos.relative(zNDir), zNDir) / 15.0f;

            HANDLE.setContactsEnabled(false);
            HANDLE.setMotor(Axis1, AngleHelper.rad((xPPower - xNPower) * 45) * mul1, 10000, 125, false, 0.0);
            HANDLE.setMotor(Axis2, AngleHelper.rad((zPPower - zNPower) * 45) * mul2, 10000, 125, false, 0.0);

            ANGLE_Y = (int) ((xPPower - xNPower) * 45) * mul1;
            ANGLE_Z = (int) ((zPPower - zNPower) * 45) * mul2;

            sendData();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void assemble() {
        final BlockPos pos = this.getBlockPos();
        final BlockPos toAssemble = pos.relative(this.getBlockState().getValue(GimbalBlock.FACING));
        final SimAssemblyHelper.AssemblyResult result;

        try {
            assert this.level != null;
            result = SimAssemblyHelper.assembleFromSingleBlock(this.level, pos, toAssemble, false, false);
            this.LAST_EXCEPTION = null;
        } catch (final AssemblyException e) {
            this.LAST_EXCEPTION = e;
            this.sendData();
            return;
        }

        this.sendData();

        final ServerSubLevel assembledSubLevel;
        final BlockPos assembleOffset;
        final BlockState link = utmBlocks.GIMBAL_PLATE.BLOCK.get().defaultBlockState()
                .setValue(GimbalBlock.FACING, this.getBlockState().getValue(GimbalBlock.FACING));

        if (result != null) {
            assembledSubLevel = (ServerSubLevel) result.subLevel();
            assembleOffset = result.offset();
        } else {
            final ServerSubLevelContainer container = (ServerSubLevelContainer) SubLevelContainer.getContainer(this.level);

            final Pose3d pose = new Pose3d();
            pose.position().set(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

            assert container != null;
            assembledSubLevel = (ServerSubLevel) container.allocateNewSubLevel(pose);
            final LevelPlot plot = assembledSubLevel.getPlot();

            final ChunkPos center = plot.getCenterChunk();
            plot.newEmptyChunk(center);
            plot.getEmbeddedLevelAccessor().setBlock(BlockPos.ZERO, link, 3);

            final BlockPos plotAnchor = plot.getCenterBlock();
            final Vector3dc centerOfMass = assembledSubLevel.getMassTracker().getCenterOfMass();
            final Vector3d subLevelCenter = JOMLConversion.atLowerCornerOf(pos);

            if (centerOfMass != null) {
                subLevelCenter.add(centerOfMass.x() - plotAnchor.getX(), centerOfMass.y() - plotAnchor.getY(), centerOfMass.z() - plotAnchor.getZ());
            } else {
                assembledSubLevel.logicalPose().rotationPoint()
                        .set(plotAnchor.getX() + 0.5, plotAnchor.getY() + 0.5, plotAnchor.getZ() + 0.5);
            }

            assembledSubLevel.logicalPose().position().set(subLevelCenter.x, subLevelCenter.y, subLevelCenter.z);

            assembleOffset = plotAnchor.subtract(pos);

            final SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
            final PhysicsPipeline pipeline = physicsSystem.getPipeline();

            final SubLevel containingSubLevel = this.getContainingSubLevel();
            if (containingSubLevel != null) {
                SubLevelAssemblyHelper.kickFromContainingSubLevel((ServerLevel) this.level, physicsSystem, pipeline, assembledSubLevel, containingSubLevel);
            }

            pipeline.teleport(assembledSubLevel, assembledSubLevel.logicalPose().position(), assembledSubLevel.logicalPose().orientation());
            assembledSubLevel.updateLastPose();

            this.level.playSound(null, pos, SimSoundEvents.SIMULATED_CONTRAPTION_MOVES.event(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }

        assert this.getLevel() != null;
        this.getLevel().setBlockAndUpdate(pos, this.getBlockState().setValue(GimbalBlock.ASSEMBLED, true));
        ASSEMBLED = true;

        ATTACHED_LEVEL = assembledSubLevel.getUniqueId();

        final BlockPos plotPos = pos.offset(assembleOffset);
        if (result != null) {
            this.getLevel().setBlockAndUpdate(plotPos, link);
        }
        final BlockEntity be = this.getLevel().getBlockEntity(plotPos);

        if (be instanceof final GimbalPlateBlockEntity plateBE) {
            plateBE.setParent(this);
            PLATE_POS = plotPos;
        }

        this.attachConstraints(assembledSubLevel, this.getConstraintPos(toAssemble, assembleOffset));
        sendData();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (HANDLE != null) {
            HANDLE.remove();
            HANDLE = null;
        }
    }

    @Override
    public void remove() {
        assert this.level != null;
        if (!this.level.isClientSide && !ASSEMBLING)
            this.destroyPlate();

        super.remove();
    }

    private @NotNull Vector3d getConstraintPos(final BlockPos relative, final BlockPos offset) {
        return JOMLConversion.toJOML(relative.offset(offset).getCenter());
    }

    public void disassemble() {
        if (this.isRemoved()) {
            return;
        }

        if (HANDLE != null) {
            HANDLE.remove();
            HANDLE = null;
        }

        if (ATTACHED_LEVEL != null) {
            final SubLevel subLevel = Objects.requireNonNull(SubLevelContainer.getContainer(this.level)).getSubLevel(ATTACHED_LEVEL);
            if (subLevel != null) {
                final BlockPos platePos = PLATE_POS;
                if (platePos != null) {
                    this.destroyPlate();

                    if (!subLevel.isRemoved()) {
                        SimAssemblyHelper.disassembleSubLevel(this.level, subLevel, platePos, this.getBlockPos(), Rotation.NONE, true);
                    } else {
                        this.level.playSound(null, platePos, SimSoundEvents.SIMULATED_CONTRAPTION_STOPS.event(), SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                }
            }
        }

        assert this.getLevel() != null;
        this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(GimbalBlock.ASSEMBLED, false));
        ASSEMBLED = false;

        ATTACHED_LEVEL = null;
        PLATE_POS = null;
        sendData();
    }

    public void destroyPlate() {
        final BlockPos platePos = PLATE_POS;
        if (platePos != null) {
            final SubLevelContainer container = SubLevelContainer.getContainer(this.level);
            if (container == null) return;

            final SubLevel subLevel = container.getSubLevel(ATTACHED_LEVEL);
            if (subLevel == null) return;

            assert this.getLevel() != null;
            if (this.getLevel().getBlockState(platePos).is(utmBlocks.GIMBAL_PLATE.BLOCK)) {
                utmBlocks.GIMBAL_PLATE.BLOCK.get().withBlockEntityDo(this.level, platePos, GimbalPlateBlockEntity::beforeAssembly);
                this.getLevel().setBlock(platePos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    public void checkPersistence(final UUID id) {
        if (PLATE_POS != null && SimLevelUtil.isAreaActuallyLoaded(this.getLevel(), PLATE_POS, 1)) {
            if (!this.getLevel().getBlockState(PLATE_POS).is(utmBlocks.GIMBAL_PLATE.BLOCK)) {
                return;
            }
        }

        final SubLevel subLevel = Objects.requireNonNull(SubLevelContainer.getContainer(this.getLevel())).getSubLevel(id);
        if (HANDLE != null && !HANDLE.isValid()) {
            HANDLE = null;
        }

        if (subLevel != null && HANDLE == null) {
            this.reattachConstraint(subLevel, true);
        }
    }

    public void reattachConstraint(final SubLevel toAttach, final boolean updatePlate) {
        final BlockPos platePos = PLATE_POS;
        if (platePos != null) {
            if (HANDLE != null) {
                HANDLE.remove();
            }

            if (updatePlate) {
                this.associatePlateWithParent();
            }

            assert this.level != null;
            final BlockState plateState = this.level.getBlockState(platePos);
            if (!plateState.is(utmBlocks.GIMBAL_PLATE.BLOCK)) return;

            final Direction plateFacing = plateState.getValue(GimbalPlateBlock.FACING);
            this.attachConstraints(toAttach, JOMLConversion.toJOML(platePos.relative(plateFacing).getCenter()));
        }
    }

    private void attachConstraints(final SubLevel toAttach, final Vector3d attachPos) {
        if (PLATE_POS == null) return;
        assert this.level != null;
        final BlockState plateState = this.level.getBlockState(PLATE_POS);
        final SubLevel sublevel = Sable.HELPER.getContaining(this);

        if (!plateState.is(utmBlocks.GIMBAL_PLATE.BLOCK)) return;

        final Vector3d anchorPos = JOMLConversion.toJOML(this.getBlockPos().relative(this.getBlockState().getValue(RotatableBlock.FACING)).getCenter());
        final Vec3 facingVec = Vec3.atLowerCornerOf(this.getBlockState().getValue(RotatableBlock.FACING).getNormal());
        final Vec3 plateFacingVec = Vec3.atLowerCornerOf(plateState.getValue(RotatableBlock.FACING).getNormal());
        Quaterniond rotation = new Quaterniond();

        if (sublevel != null) {
            toAttach.logicalPose().orientation().conjugate(rotation);
            rotation = sublevel.logicalPose().orientation().mul(rotation);
        } else {
            rotation = toAttach.logicalPose().orientation();
        }

        final FreeConstraintConfiguration constraint = new FreeConstraintConfiguration(
                anchorPos,
                attachPos.sub(JOMLConversion.toJOML(plateFacingVec.scale(0.8001f))),
                rotation
        );

        final ServerSubLevelContainer container = SubLevelContainer.getContainer((ServerLevel) this.getLevel());
        assert container != null;
        final PhysicsPipeline pipeline = container.physicsSystem().getPipeline();

        HANDLE = pipeline.addConstraint((ServerSubLevel) Sable.HELPER.getContaining(this), (ServerSubLevel) toAttach, constraint);

        HANDLE.setMotor(ConstraintJointAxis.LINEAR_X, 0, 30000, 500, false, 0.0);
        HANDLE.setMotor(ConstraintJointAxis.LINEAR_Y, 0, 30000, 500, false, 0.0);
        HANDLE.setMotor(ConstraintJointAxis.LINEAR_Z, 0, 30000, 500, false, 0.0);

        HANDLE.setMotor(ConstraintJointAxis.ANGULAR_X, 0, 10000, 500, false, 0.0);
        HANDLE.setMotor(ConstraintJointAxis.ANGULAR_Y, 0, 10000, 500, false, 0.0);
        HANDLE.setMotor(ConstraintJointAxis.ANGULAR_Z, 0, 10000, 500, false, 0.0);
    }

    public void associatePlateWithParent() {
        if (PLATE_POS != null) {
            assert this.getLevel() != null;
            if (this.getLevel().getBlockState(PLATE_POS).is(utmBlocks.GIMBAL_PLATE.BLOCK)) {
                final GimbalPlateBlockEntity plate = (GimbalPlateBlockEntity) this.getLevel().getBlockEntity(PLATE_POS);
                assert plate != null;
                plate.setParent(this);
            }
        }
    }


    @Override
    public boolean sable$migrateData(Map<ServerSubLevel, ServerSubLevel> conversions, GimbalBlockEntity oldBE, Map<ServerSubLevel, SubLevelAssemblyHelper.AssemblyTransform> transforms) {
        final BlockPos platePos = oldBE.PLATE_POS;
        if (platePos != null && SableCompanion.INSTANCE.getContaining(oldBE) instanceof final ServerSubLevel oldLevel && conversions.get(oldLevel) instanceof final ServerSubLevel newLevel) {
            final ServerSubLevel plateLevel = (ServerSubLevel) Objects.requireNonNull(SubLevelContainer.getContainer(oldLevel.getLevel())).getSubLevel(oldBE.ATTACHED_LEVEL); // TODO: not this

            ATTACHED_LEVEL = newLevel.getUniqueId();
            PLATE_POS = transforms.get(newLevel).apply(platePos);
            reattachConstraint(conversions.get(plateLevel), true);
            return true;
        }
        return false;
    }

    @Override
    public void sable$cleanLevelNBT(CompoundTag tag) {
        tag.remove("SubLevelID");
        tag.remove("PlatePos");
    }

    @Override
    protected void write(final CompoundTag compound, final HolderLookup.Provider registries, final boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        final SubLevelSchematicSerializationContext schematicContext = SubLevelSchematicSerializationContext.getCurrentContext();

        if (ATTACHED_LEVEL != null && schematicContext != null) {
            final SubLevelSchematicSerializationContext.SchematicMapping mapping = schematicContext.getMapping(ATTACHED_LEVEL);

            if (mapping != null) {
                ATTACHED_LEVEL = mapping.newUUID();
                PLATE_POS = mapping.transform().apply(PLATE_POS);
            } else {
                ATTACHED_LEVEL = null;
                PLATE_POS = null;
            }
        }

        if (ATTACHED_LEVEL != null)
            compound.putUUID("SubLevelID", ATTACHED_LEVEL);

        if (PLATE_POS != null)
            compound.put("PlatePos", NbtUtils.writeBlockPos(PLATE_POS));

        compound.putInt("AngleY", ANGLE_Y);
        compound.putInt("AngleZ", ANGLE_Z);

        compound.putBoolean("Assembled", ASSEMBLED);
    }

    @Override
    protected void read(final CompoundTag compound, final HolderLookup.Provider registries, final boolean clientPacket) {
        super.read(compound, registries, clientPacket);

        final SubLevelSchematicSerializationContext schematicContext = SubLevelSchematicSerializationContext.getCurrentContext();

        SubLevelSchematicSerializationContext.SchematicMapping mapping = null;

        if (compound.hasUUID("SubLevelID")) {
            UUID subLevelID = compound.getUUID("SubLevelID");

            if (schematicContext != null)
                mapping = schematicContext.getMapping(subLevelID);

            if (mapping != null)
                subLevelID = mapping.newUUID();

            ATTACHED_LEVEL = subLevelID;
        }

        if (compound.contains("PlatePos"))
            PLATE_POS = NbtUtils.readBlockPos(compound, "PlatePos").orElseThrow();

        if (compound.contains("AngleY"))
            ANGLE_Y = compound.getInt("AngleY");

        if (compound.contains("AngleZ"))
            ANGLE_Z = compound.getInt("AngleZ");

        if (compound.contains("Assembled"))
            ASSEMBLED = compound.getBoolean("Assembled");

        this.LAST_EXCEPTION = AssemblyException.read(compound, registries);
    }

    private @Nullable SubLevel getContainingSubLevel() {
        return Sable.HELPER.getContaining(this);
    }

    public void beforeAssembly() {
        ASSEMBLING = true;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        utmLang.text("Collection Info:").style(ChatFormatting.WHITE).forGoggles(tooltip, 0);
        utmLang.text("X angle:").style(ChatFormatting.GRAY).space().add(utmLang.text(ANGLE_Y + "°").style(ChatFormatting.AQUA)).forGoggles(tooltip);
        utmLang.text("Y angle:").style(ChatFormatting.GRAY).space().add(utmLang.text(ANGLE_Z + "°").style(ChatFormatting.AQUA)).forGoggles(tooltip);

        return true;
    }
}
