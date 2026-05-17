package com.nadia.utm.networking;

import com.nadia.utm.client.ui.TabMenuLayer;
import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.events.OxygenPayloadEvent;
import com.nadia.utm.event.events.SyncSealedDataEvent;
import com.nadia.utm.event.utmEvents;
import com.nadia.utm.gui.GlintMenu;
import com.nadia.utm.networking.payloads.*;
import com.nadia.utm.projectile.DroplessArrow;
import com.nadia.utm.registry.attachment.utmAttachments;
import com.nadia.utm.registry.item.utmItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ForceLoad
public class utmNetworking {
    public static PayloadRegistrar REGISTRAR;
    public static final List<Runnable> CALLBACKS = new ArrayList<>();

    public static <P extends CustomPacketPayload> void server(PacketDef<P> def, IPayloadHandler<P> consumer) {
        REGISTRAR.playToServer(def.type(), def.codec(), consumer);
    }

    public static <P extends CustomPacketPayload> void client(PacketDef<P> def, IPayloadHandler<P> consumer) {
        REGISTRAR.playToClient(def.type(), def.codec(), consumer);
    }

    public static void registerNetworkingEvents(final RegisterPayloadHandlersEvent event) {
        REGISTRAR = event.registrar("1");

        server(DropGravePayload.DEF, (payload, context) -> context.enqueueWork(() -> DropGravePayload.drop(payload, context)));
        server(GlintSyncPayload.DEF, (payload, context) -> context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof GlintMenu menu) menu.unpack(payload);
        }));

        client(TabLayerPayload.DEF, (payload, context) -> context.enqueueWork(() -> TabMenuLayer.CACHE = payload.players()));

        server(RequestSealedDataPayload.DEF, (payload, context) -> context.enqueueWork(() -> {
            Level level = context.player().level();
            ChunkAccess chunk = level.getChunk(payload.pos().x, payload.pos().z);
            if (chunk.hasData(utmAttachments.SEALED_AIR)) {
                context.reply(new SyncSealedDataPayload(payload.pos(), chunk.getData(utmAttachments.SEALED_AIR)));
            }
        }));

        client(SyncSealedDataPayload.DEF, (payload, context) -> context.enqueueWork(() -> NeoForge.EVENT_BUS.post(new SyncSealedDataEvent(payload))));

        server(MyAwesomeKarkParticlePayload.DEF, (payload, context) -> context.enqueueWork(() -> {
            Player player = context.player();
            Vector3f pos = payload.pos();
            Vector3f dir = payload.dir();
            if (player.level() instanceof ServerLevel slevel) {
                slevel.sendParticles(ParticleTypes.SWEEP_ATTACK, pos.x + dir.x, pos.y + dir.y + player.getEyeHeight() - 0.25f, pos.z + dir.z, 0, payload.xOff(), 0.0F, payload.yOff(), 0.0F);
                DroplessArrow proj = new DroplessArrow(EntityType.ARROW, slevel);

                float pitch = (float) Math.toDegrees(Math.asin(-dir.y()));
                float yaw = (float) Math.toDegrees(Math.atan2(-dir.x(), dir.z()));

                proj.shootFromRotation(player, pitch, yaw, 0, 1.25F, 0);
                proj.setPos(player.getEyePosition().add(dir.x, dir.y, dir.z));
                slevel.addFreshEntity(proj);
                slevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_ATTACK_SWEEP, Objects.requireNonNull(slevel.getRandomPlayer()).getSoundSource(), 1.0F, 1.0F);
                if (!player.isCreative()) {
                    ItemStack itemstack = player.getMainHandItem();
                    itemstack.setDamageValue(itemstack.getDamageValue() + 1);
                }
            }
        }));
        server(Sword2AttackPayload.DEF, (payload, context) -> context.enqueueWork(() -> {
            Player player = context.player();
            Vector3f pos = payload.pos();
            if (player.level() instanceof ServerLevel slevel) {
                ItemStack itemstack = player.getMainHandItem();
                boolean doom = !(itemstack.is(utmItems.SWORD2.get()));
                if (!player.isCreative() && !doom) {
                    itemstack.setDamageValue(itemstack.getDamageValue() + 1);
                }
                //kill people with hamers
                //#TEAMYELLOW
                slevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.VILLAGER_WORK_WEAPONSMITH, Objects.requireNonNull(slevel.getRandomPlayer()).getSoundSource(), 0.25F, 0.5F);
                if (doom)
                    slevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ANVIL_LAND, Objects.requireNonNull(slevel.getRandomPlayer()).getSoundSource(), 0.125F, 0.5F);

                for(LivingEntity livingentity2 : slevel.getEntitiesOfClass(LivingEntity.class, new AABB(pos.x-1,pos.y-0.2,pos.z-1,pos.x+1,pos.y+0.2,pos.z+1).inflate(5))) {
                    if (livingentity2!=player && (livingentity2.position().distanceTo( new Vec3(pos.x,livingentity2.position().y,pos.z))) <7 ) {
                        livingentity2.setInvulnerable(false); //looking to change IFrames :)
                        livingentity2.hurt(player.damageSources().playerAttack(player),3+(doom ? 7 : 0));
                        if (doom) {
                            player.setInvulnerable(false); //looking to change IFrames again :) //note: it doesnt work here or above so find a fix in the morning
                            player.hurt(player.damageSources().cramming(), 2);
                        }
                        Vec3 pos2 = livingentity2.position();
                        slevel.sendParticles(ParticleTypes.CRIT,pos2.x,pos2.y,pos2.z,5,0,0,0,0);
                        slevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.PLAYER_ATTACK_CRIT, Objects.requireNonNull(slevel.getRandomPlayer()).getSoundSource(), 0.20F, 1.0F);

                    }
                }
            }
        }));
        REGISTRAR.playBidirectional(
                GetOxygenPayload.TYPE,
                GetOxygenPayload.CODEC,
                (payload, context) -> {
                    if (context.flow() == PacketFlow.SERVERBOUND) {
                        context.enqueueWork(() -> {
                            UUID uuid = UUID.fromString(payload.id());
                            Player player = context.player();
                            if (player.level() instanceof ServerLevel level && player instanceof ServerPlayer sPlayer) {
                                Entity entity = level.getEntity(uuid);
                                if (entity != null) {
                                    Integer oxygen = entity.getExistingDataOrNull(utmAttachments.TEMPORARY_OXYGEN);
                                    if (oxygen == null)
                                        oxygen = -1;
                                    context.reply(new GetOxygenPayload(payload.id(), oxygen));
                                }
                            }
                        });
                    } else {
                        context.enqueueWork(() -> NeoForge.EVENT_BUS.post(new OxygenPayloadEvent(payload)));
                    }
                }
        );

        for (Runnable c : CALLBACKS)
            c.run();
    }

    public static void addCallback(Runnable c) {
        if (REGISTRAR != null)
            c.run();
        else
            CALLBACKS.add(c);
    }

    static {
        utmEvents.register(RegisterPayloadHandlersEvent.class, utmNetworking::registerNetworkingEvents);
    }
}