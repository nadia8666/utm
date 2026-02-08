package com.nadia.utm.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;

public class NetherytraItem extends ElytraItem {

    public NetherytraItem(Properties properties) {
        super(properties);
    }
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.NETHERITE_INGOT);
    }
    @Override
    public Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }


    public double createRandom() {
        return (Math.random()-0.5)*2;
    };

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        var pos = entity.position();
        if (entity.level().isClientSide) {
            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.WHITE_ASH, pos.x+createRandom()*30, pos.y+createRandom()*30, pos.z+createRandom()*30, 0, 0, 0);
        }
        if (!entity.level().isClientSide) {
            if (entity.level() instanceof ServerLevel level) {
                var lA = entity.getLookAngle();
                level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,pos.x,pos.y,pos.z,1,0,0,0,0);
            }


            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                if (nextFlightTick % 20 == 0) {
                    stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
                }

                entity.gameEvent(GameEvent.ELYTRA_GLIDE);
            }
        }

        return true;
    }
}
