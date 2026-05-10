package com.nadia.utm.tool;

import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import java.util.List;

import static java.lang.Math.floor;

public class ChorlieKarkSword extends SwordItem {

    public ChorlieKarkSword(Tier tier, Properties properties) {
        super(tier, properties);
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemstack = player.getItemInHand(usedHand);
        double d0 = (double)(-Mth.sin(player.getYRot() * ((float)Math.PI / 180F)));
        double d1 = (double)Mth.cos(player.getYRot() * ((float)Math.PI / 180F));
        if (player.level() instanceof ServerLevel) {
            ((ServerLevel)player.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX() + d0, player.getY((double)0.5F), player.getZ() + d1, 0, d0, (double)0.0F, d1, (double)0.0F);
        }

        return InteractionResultHolder.pass(itemstack);

    }
}
