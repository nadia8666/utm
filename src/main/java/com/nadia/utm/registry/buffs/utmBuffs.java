package com.nadia.utm.registry.buffs;

import com.nadia.utm.buff.InterdictedBuff;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class utmBuffs {
    public static final DeferredRegister<MobEffect> BUFFS = DeferredRegister.create(Registries.MOB_EFFECT, "utm");
    public static final Supplier<InterdictedBuff> INTERDICTED = BUFFS.register("interdicted", () -> new InterdictedBuff(
            //Can be either BENEFICIAL, NEUTRAL or HARMFUL. Used to determine the potion tooltip color of this effect.
            MobEffectCategory.HARMFUL,
            //The color of the effect particles.
            0xffffff
    ));
}
