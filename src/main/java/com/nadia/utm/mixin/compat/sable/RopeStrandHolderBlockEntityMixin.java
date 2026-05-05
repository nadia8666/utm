package com.nadia.utm.mixin.compat.sable;

import com.nadia.utm.compat.BlockEntitySubLevelActorExtensions;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachment;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.RopeAttachmentPoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(value = RopeStrandHolderBlockEntity.class, remap = false)
public interface RopeStrandHolderBlockEntityMixin extends BlockEntitySubLevelActorExtensions<RopeStrandHolderBlockEntity> {
    @Override
    default boolean sable$migrateData(final Map<ServerSubLevel, ServerSubLevel> conversions, final RopeStrandHolderBlockEntity oldBE, final Map<ServerSubLevel, SubLevelAssemblyHelper.AssemblyTransform> transforms) {
        if (!(oldBE.getBehavior().getOwnedStrand() instanceof final ServerRopeStrand strand)) return false;
        if (!(strand.getAttachment(RopeAttachmentPoint.START) instanceof final RopeAttachment start && strand.getAttachment(RopeAttachmentPoint.END) instanceof final RopeAttachment end))
            return false;

        ServerSubLevel oldStart = null, oldEnd = null;
        for (final ServerSubLevel level : conversions.keySet()) {
            if (level.getUniqueId().equals(start.subLevelID())) oldStart = level;
            if (level.getUniqueId().equals(end.subLevelID())) oldEnd = level;
            if (oldStart != null && oldEnd != null) break;
        }

        if (oldStart == null || oldEnd == null) return false;

        final RopeStrandHolderBlockEntity newStart = (RopeStrandHolderBlockEntity) conversions.get(oldStart).getLevel().getBlockEntity(transforms.get(oldStart).apply(start.blockAttachment()));
        final RopeStrandHolderBlockEntity newEnd = (RopeStrandHolderBlockEntity) conversions.get(oldEnd).getLevel().getBlockEntity(transforms.get(oldEnd).apply(end.blockAttachment()));

        if (newStart == null || newEnd == null) return false;

        newStart.getBehavior().createRope(newEnd.getBehavior());
        return true;
    }

    default void sable$cleanLevelNBT(CompoundTag tag) {
        tag.remove("HasRopeAttached");
        tag.remove("Strand");
        tag.putBoolean("OwnStrand", false);
    }
}
