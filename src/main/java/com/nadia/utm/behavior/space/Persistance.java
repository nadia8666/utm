package com.nadia.utm.behavior.space;

import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.util.AdvancementUtil;
import com.nadia.utm.utm;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;

import static com.nadia.utm.registry.attachment.utmAttachments.ENTERED_2313AG;

class Persistance {
    public static void checkPersistance(ServerPlayer sPlayer, ServerLevel a23Level, boolean enteredAG, boolean inAG) {
        if (enteredAG && !inAG) {
            if (a23Level != null) {
                int x = sPlayer.blockPosition().getX();
                int z = sPlayer.blockPosition().getZ();
                int height = Positioning.getSurface(a23Level, x, z);

                if (height == -13579) {
                    height = -63;
                    a23Level.setBlock(new BlockPos(x, -64, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
                }
                sPlayer.teleportTo(a23Level, sPlayer.getX(), height, sPlayer.getZ(), sPlayer.getYRot(), sPlayer.getXRot());
            }
        } else if (!enteredAG && inAG) {
            sPlayer.setData(ENTERED_2313AG, true);
            sPlayer.setRespawnPosition(utmDimensions.AG_KEY, sPlayer.blockPosition(), sPlayer.getYRot(), true, true);
        }

        if (inAG)
            AdvancementUtil.AwardAdvancement(sPlayer, utm.key("2313ag/root"));
    }
}
