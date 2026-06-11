package com.nadia.utm.mixin;


import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.registry.particle.utmParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, remap = false)
public abstract class HitEffectMixin extends LivingEntity {

    protected HitEffectMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
        //adapted from beemod code that nadia wrote thanks nadia
        // the adapted part is most of hte code lol i Didnt relaize until the end but the inner function is basically the exact same in this
        // but i wrote it all myself initially and then realize ohh god i can just use DECOMPILED CODE instead :) so hi nadia
    @Inject(at = @At("HEAD"), method = "sweepAttack", cancellable = true)
    private void onSweep(CallbackInfo cir) {
        boolean did_trigger = false;
        if (getMainHandItem().is(utmTools.ARID_SWORD.get()) ) {
            double d0 = (double)(-Mth.sin(this.getYRot() * ((float)Math.PI / 180F)));
            double d1 = (double)Mth.cos(this.getYRot() * ((float)Math.PI / 180F));
            if (this.level() instanceof ServerLevel) {
                ((ServerLevel) this.level()).sendParticles(utmParticles.RED_SWEEP.get(), this.getX() + d0, this.getY((double)0.5F), this.getZ() + d1, 0, d0, 0.0, d1, 0.0);
            }

            did_trigger = true;
        }

        // if any of the items activated their custom effects, stop the default effect from happening
        if (did_trigger) {
            cir.cancel();
        }
    }
}
