package com.nadia.utm.behavior.space;

import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.planets.utmPlanets;
import com.nadia.utm.util.AdvancementUtil;
import com.nadia.utm.utm;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.DimensionTransition;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.Objects;

class Persistance {
    public static void checkPersistance(ServerPlayer sPlayer, String currentPlanet) {
        ServerLevel level = sPlayer.serverLevel();
        MinecraftServer server = sPlayer.getServer();
        if (server == null) return;

        utmPlanets.Planet current = utmPlanets.get(level);

        if (current != null) {
            if (!Objects.equals(currentPlanet, current.IDENTIFIER) && !(FMLEnvironment.production && current.IDENTIFIER.equals("earth"))) { // dont allow backwarping to earth in prod
                int x = sPlayer.blockPosition().getX();
                int z = sPlayer.blockPosition().getZ();
                int y = Positioning.getSurface(level, x, z);

                if (y == -13579) {
                    y = -63;
                    level.setBlock(new BlockPos(x, -64, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
                }

                if (!sPlayer.getRespawnDimension().equals(current.KEY))
                    sPlayer.setRespawnPosition(utmDimensions.AG_KEY, new BlockPos(x, y, z), sPlayer.getYRot(), true, false);

                sPlayer.setData(utmAttachments.REGISTERED_PLANET, current.IDENTIFIER);
                currentPlanet = current.IDENTIFIER;
            }

            if (current.equals(utmPlanets.AG23)) AdvancementUtil.AwardAdvancement(sPlayer, utm.key("2313ag/suffocate"));
        }

        if (!Objects.equals(currentPlanet, "none")) {
            utmPlanets.Planet targetPlanet = utmPlanets.get(currentPlanet);
            if (targetPlanet != null && !targetPlanet.equals(current) && server.getLevel(targetPlanet.KEY) instanceof ServerLevel target) {
                int x = sPlayer.blockPosition().getX();
                int z = sPlayer.blockPosition().getZ();
                int y = Positioning.getSurface(target, x, z);

                if (y == -13579) {
                    y = -63;
                    target.setBlock(new BlockPos(x, -64, z), Blocks.COBBLESTONE.defaultBlockState(), 3);
                }

                sPlayer.changeDimension(new DimensionTransition(
                        target,
                        sPlayer.position().with(Direction.Axis.Y, y),
                        sPlayer.getDeltaMovement(),
                        sPlayer.getYRot(),
                        sPlayer.getXRot(),
                        (e) -> {
                        }
                ));
            }
        }

    }
}
