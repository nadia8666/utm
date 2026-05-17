package com.nadia.utm.behavior.space;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.registry.dimension.utmDimensions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@ForceLoad
public class SpaceEntityManager extends SavedData {
    public static Set<ResourceKey<Level>> LOADING_FOR = new HashSet<>();
    public final List<CompoundTag> entityNBTList = new ArrayList<>();
    public final Map<UUID, Entity> entities = new HashMap<>();
    public boolean is_loading = true;

    public static SpaceEntityManager create() {
        return new SpaceEntityManager();
    }

    public void addEntity(Entity entity) {
        if (!entities.containsKey(entity.getUUID())) {
            entities.put(entity.getUUID(), entity);
            setDirty();
        }
    }

    public void tick(ServerLevel level) {
        AtomicBoolean changed = new AtomicBoolean(false);
        entities.values().removeIf(entity -> {
            if (!entity.isAlive() || !entity.level().dimension().equals(utmDimensions.SPACE_KEY)) {
                changed.set(true);
                return true;
            }

            boolean ticking = level.getChunkSource().isPositionTicking(entity.chunkPosition().toLong());
            if (!ticking) {
                entity.tick();

                entity.setPos(entity.getX() + entity.getDeltaMovement().x, entity.getY() + entity.getDeltaMovement().y, entity.getZ() + entity.getDeltaMovement().z);

                if (level.getGameTime() % 2 == 0) syncToAllPlayers(entity);
            }
            return false;
        });

        if (changed.get()) setDirty();
    }

    private void syncToAllPlayers(Entity entity) {
        if (!(entity.level() instanceof ServerLevel sLevel)) return;

        ClientboundAddEntityPacket addPacket = new ClientboundAddEntityPacket(entity, 0, entity.blockPosition());
        ClientboundTeleportEntityPacket posPacket = new ClientboundTeleportEntityPacket(entity);

        for (ServerPlayer player : sLevel.players()) {
            if (player.getUUID().equals(entity.getUUID())) continue;
            player.connection.send(addPacket);
            player.connection.send(posPacket);
        }
    }

    public static SpaceEntityManager load(CompoundTag nbt, HolderLookup.Provider lookup, ServerLevel level) {
        LOADING_FOR.add(level.dimension());

        SpaceEntityManager manager = create();
        ListTag list = nbt.getList("Entities", Tag.TAG_COMPOUND);

        manager.is_loading = true;

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entityTag = list.getCompound(i);
            EntityType.create(entityTag, level).ifPresent(entity -> {
                level.addFreshEntity(entity);
                manager.addEntity(entity);
                manager.syncToAllPlayers(entity);
            });
        }

        manager.is_loading = false;

        LOADING_FOR.remove(level.dimension());

        return manager;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider lookup) {
        ListTag list = new ListTag();
        for (Entity entity : entities.values()) {
            CompoundTag entityData = new CompoundTag();
            entity.save(entityData);
            list.add(entityData);
        }
        nbt.put("Entities", list);
        return nbt;
    }

    public static SpaceEntityManager getInstance(ServerLevel slevel) {
        return slevel.getDataStorage().computeIfAbsent(new Factory<>(SpaceEntityManager::create, (CompoundTag nbt, HolderLookup.Provider lookup) -> load(nbt, lookup, slevel)), "space_state");
    }

    static {
        utmEvents.register(LevelTickEvent.Post.class, event -> {
            if (event.getLevel() instanceof ServerLevel slevel && slevel.dimension().equals(utmDimensions.SPACE_KEY)) {
                SpaceEntityManager manager = getInstance(slevel);
                manager.tick(slevel);
            }
        });

        utmEvents.register(EntityJoinLevelEvent.class, event -> {
            if (event.getLevel().dimension().equals(utmDimensions.SPACE_KEY) && event.getLevel() instanceof ServerLevel slevel) {
                if (LOADING_FOR.contains(event.getLevel().dimension())) return;

                SpaceEntityManager manager = getInstance(slevel);
                if (manager.is_loading) return;
                manager.addEntity(event.getEntity());
            }
        });
    }
}
