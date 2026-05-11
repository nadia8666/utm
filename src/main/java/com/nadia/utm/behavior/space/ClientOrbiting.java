package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.planets.utmPlanets;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Vector3d;
import oshi.util.tuples.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.nadia.utm.behavior.space.Orbiting.getBodyForEntity;
import static com.nadia.utm.behavior.space.Orbiting.getBodyForSublevel;

@SuppressWarnings("UnstableApiUsage")
@ForceLoad(dist = Dist.CLIENT)
public class ClientOrbiting {
    static final Map<utmPlanets.Planet, Pair<Vector3d, Vector3d>> TRANSFORM_LIST = new HashMap<>();
    static final Map<utmPlanets.Planet, Vector3d> LAST_PLANET_POSITIONS = new HashMap<>();
    static final Map<utmPlanets.Planet, Set<SubLevel>> LEVELS_TO_MOVE = new HashMap<>();
    static final Map<utmPlanets.Planet, Set<Entity>> ENTITIES_TO_MOVE = new HashMap<>();
    static final Set<SubLevel> PROCESSED_LEVELS = new HashSet<>();
    static final Vector3d CALC = new Vector3d();
    static final Vector3d CALC2 = new Vector3d();

    static {
        utmEvents.register(ClientTickEvent.Pre.class, event -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if (level == null) return;

            if (!level.dimension().equals(utmDimensions.SPACE_KEY)) return;

            SubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) return;

            long time = level.dayTime();
            float partialTicks = mc.getTimer().getGameTimeDeltaTicks();

            for (utmPlanets.Planet planet : utmPlanets.ALL_PLANETS) {
                Vector3d currentPos = planet.ORBIT().getPosition(time, 0);
                Vector3d lastPos = LAST_PLANET_POSITIONS.getOrDefault(planet, currentPos);

                TRANSFORM_LIST.put(planet, new Pair<>(lastPos, currentPos));
                LAST_PLANET_POSITIONS.put(planet, currentPos);
            }

            PROCESSED_LEVELS.clear();
            for (Set<SubLevel> value : LEVELS_TO_MOVE.values()) value.clear();
            for (Set<Entity> value : ENTITIES_TO_MOVE.values()) value.clear();

            for (SubLevel sublevel : container.getAllSubLevels()) {
                if (sublevel.isRemoved() || PROCESSED_LEVELS.contains(sublevel)) continue;

                utmPlanets.Planet body = getBodyForSublevel(sublevel);
                Set<SubLevel> affectedLevels = LEVELS_TO_MOVE.computeIfAbsent(body, k -> new HashSet<>());

                for (SubLevel connected : SubLevelHelper.getConnectedChain(sublevel)) {
                    PROCESSED_LEVELS.add(connected);
                    affectedLevels.add(connected);
                }

                PROCESSED_LEVELS.add(sublevel);
                affectedLevels.add(sublevel);
            }

            if (mc.player instanceof LocalPlayer player) {
                if (Sable.HELPER.getTrackingOrVehicleSubLevel(player) instanceof SubLevel sublevel) {
                    Set<Entity> affectedEntities = null;
                    for (Map.Entry<utmPlanets.Planet, Set<SubLevel>> entry : LEVELS_TO_MOVE.entrySet()) {
                        if (entry.getValue().equals(sublevel)) {
                            affectedEntities = ENTITIES_TO_MOVE.computeIfAbsent(entry.getKey(), k -> new HashSet<>());
                            break;
                        }
                    }
                    if (affectedEntities != null) {
                        affectedEntities.add(player);
                    }
                } else {
                    utmPlanets.Planet body = getBodyForEntity(player);
                    Set<Entity> affectedEntities = ENTITIES_TO_MOVE.computeIfAbsent(body, k -> new HashSet<>());
                    affectedEntities.add(player);
                }
            }
        });

        utmEvents.register(ClientTickEvent.Post.class, event -> {
            Minecraft mc = Minecraft.getInstance();
            ClientLevel level = mc.level;
            if (level == null) return;
            if (!level.dimension().equals(utmDimensions.SPACE_KEY)) return;

            SubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null) return;

            LEVELS_TO_MOVE.forEach((body, sublevels) -> {
                Pair<Vector3d, Vector3d> transform = TRANSFORM_LIST.get(body);
                if (transform == null) return;

                CALC.set(transform.getB()).sub(transform.getA());
                Vec3 deltaVec = new Vec3(CALC.x, CALC.y, CALC.z);

                for (SubLevel sublevel : sublevels) {
                    if (sublevel.isRemoved()) continue;

                    sublevel.logicalPose().position().add(CALC, CALC2);
                    sublevel.logicalPose().position().set(CALC2);
                }
            });

            ENTITIES_TO_MOVE.forEach((body, entities) -> {
                Pair<Vector3d, Vector3d> transform = TRANSFORM_LIST.get(body);
                if (transform == null) return;

                CALC.set(transform.getB()).sub(transform.getA());
                Vec3 delta = new Vec3(CALC.x, CALC.y, CALC.z);

                for (Entity entity : entities)
                    if (!entity.isRemoved())
                        if (entity instanceof LocalPlayer lp)
                            lp.setPos(lp.position().add(delta));
            });
        });
    }
}
