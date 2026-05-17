package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.dimension.utmDimensions;
import com.nadia.utm.registry.planets.utmPlanets;
import com.nadia.utm.util.SableUtil;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelHelper;
import dev.ryanhcode.sable.api.physics.PhysicsPipeline;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.joml.Vector3d;
import oshi.util.tuples.Pair;

import java.util.*;

@ForceLoad
@SuppressWarnings("UnstableApiUsage")
class Orbiting {
    static final Map<utmPlanets.Planet, Pair<Vector3d, Vector3d>> TRANSFORM_LIST = new HashMap<>();
    static final Map<utmPlanets.Planet, Vector3d> LAST_PLANET_POSITIONS = new HashMap<>();
    static final Map<utmPlanets.Planet, Set<ServerSubLevel>> LEVELS_TO_MOVE = new HashMap<>();
    static final Map<utmPlanets.Planet, Set<Entity>> ENTITIES_TO_MOVE = new HashMap<>();
    static final Set<ServerSubLevel> PROCESSED_LEVELS = new HashSet<>();
    static final Vector3d CALC = new Vector3d();
    static final Vector3d CALC2 = new Vector3d();

    public static utmPlanets.Planet getBodyForSublevel(SubLevel sublevel) {
        Vector3d levelPos = sublevel.logicalPose().position();
        utmPlanets.Planet bestBody = utmPlanets.SUN;
        double bestDistSq = Double.MAX_VALUE;

        for (utmPlanets.Planet planet : utmPlanets.ALL_PLANETS) {
            double distSq = TRANSFORM_LIST.get(planet).getA().distanceSquared(levelPos);

            if (distSq <= bestDistSq && distSq <= planet.ORBIT().getAreaOfInfluenceSquared()) {
                bestDistSq = distSq;
                bestBody = planet;
            }
        }
        return bestBody;
    }

    public static utmPlanets.Planet getBodyForEntity(Entity entity) {
        Vector3d pos = SableUtil.toVector3d(entity.getPosition(0));
        utmPlanets.Planet bestBody = utmPlanets.SUN;
        double bestDistSq = Double.MAX_VALUE;

        for (utmPlanets.Planet planet : utmPlanets.ALL_PLANETS) {
            double distSq = TRANSFORM_LIST.get(planet).getA().distanceSquared(pos);

            if (distSq <= bestDistSq && distSq <= planet.ORBIT().getAreaOfInfluenceSquared()) {
                bestDistSq = distSq;
                bestBody = planet;
            }
        }
        return bestBody;
    }

    static void moveEntity(Entity entity, Vec3 delta) {
        if (entity instanceof Player) return;

        entity.teleportRelative(delta.x, delta.y, delta.z);
    }

    static {
        utmEvents.register(LevelTickEvent.Pre.class, event -> {
            if (event.getLevel() instanceof ServerLevel level) {
                if (!level.dimension().equals(utmDimensions.SPACE_KEY)) return;

                ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
                if (container == null) return;

                long time = level.dayTime();

                for (utmPlanets.Planet planet : utmPlanets.ALL_PLANETS) {
                    Vector3d currentPos = planet.ORBIT().getPosition(time, 0);
                    Vector3d lastPos = LAST_PLANET_POSITIONS.getOrDefault(planet, currentPos);

                    TRANSFORM_LIST.put(planet, new Pair<>(lastPos, currentPos));
                    LAST_PLANET_POSITIONS.put(planet, currentPos);
                }

                PROCESSED_LEVELS.clear();
                for (Set<ServerSubLevel> value : LEVELS_TO_MOVE.values()) value.clear();
                for (Set<Entity> value : ENTITIES_TO_MOVE.values()) value.clear();

                for (ServerSubLevel sublevel : container.getAllSubLevels()) {
                    if (sublevel.isRemoved() || PROCESSED_LEVELS.contains(sublevel)) continue;

                    utmPlanets.Planet body = getBodyForSublevel(sublevel);
                    Set<ServerSubLevel> affectedLevels = LEVELS_TO_MOVE.computeIfAbsent(body, k -> new HashSet<>());

                    for (SubLevel connected : SubLevelHelper.getConnectedChain(sublevel)) {
                        ServerSubLevel s = (ServerSubLevel) connected;
                        PROCESSED_LEVELS.add(s);
                        affectedLevels.add(s);
                    }

                    PROCESSED_LEVELS.add(sublevel);
                    affectedLevels.add(sublevel);
                }

                for (Entity entity : level.getEntities().getAll()) {
                    if (!entity.level().equals(level)) continue;

                    if (Sable.HELPER.getTrackingOrVehicleSubLevel(entity) == null) {
                        utmPlanets.Planet body = getBodyForEntity(entity);
                        Set<Entity> affectedEntities = ENTITIES_TO_MOVE.computeIfAbsent(body, k -> new HashSet<>());
                        affectedEntities.add(entity);
                    }
                }
            }
        });

        utmEvents.register(LevelTickEvent.Post.class, event -> {
            if (event.getLevel() instanceof ServerLevel level) {
                if (!level.dimension().equals(utmDimensions.SPACE_KEY)) return;

                ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
                if (container == null) return;

                PhysicsPipeline pipeline = container.physicsSystem().getPipeline();
                if (pipeline == null) return;

                LEVELS_TO_MOVE.forEach((body, sublevels) -> {
                    Pair<Vector3d, Vector3d> transform = TRANSFORM_LIST.get(body);
                    if (transform == null) return;

                    CALC.set(transform.getB()).sub(transform.getA());
                    Vec3 deltaVec = new Vec3(CALC.x, CALC.y, CALC.z);

                    for (ServerSubLevel sublevel : sublevels) {
                        if (sublevel.isRemoved()) continue;
                        sublevel.logicalPose().position().add(CALC, CALC2);
                        pipeline.teleport(sublevel, CALC2, sublevel.logicalPose().orientation());

                        for (UUID uuid : sublevel.getTrackingPlayers()) {
                            Entity entity = level.getEntity(uuid);
                            if (entity != null) moveEntity(entity, deltaVec);
                        }
                    }
                });

                ENTITIES_TO_MOVE.forEach((body, entities) -> {
                    Pair<Vector3d, Vector3d> transform = TRANSFORM_LIST.get(body);
                    if (transform == null) return;

                    CALC.set(transform.getB()).sub(transform.getA());
                    Vec3 deltaVec = new Vec3(CALC.x, CALC.y, CALC.z);

                    for (Entity entity : entities) {
                        if (!entity.isRemoved()) moveEntity(entity, deltaVec);
                    }
                });
            }
        });
    }
}