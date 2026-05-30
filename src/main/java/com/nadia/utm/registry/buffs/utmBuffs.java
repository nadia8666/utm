package com.nadia.utm.registry.buffs;

import com.nadia.utm.buff.InterdictedBuff;
import com.nadia.utm.buff.utmMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
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
    public static final Supplier<utmMobEffect> NORMALIZE = BUFFS.register("normalize", () -> new utmMobEffect(
            MobEffectCategory.HARMFUL,
            0xffffff
    ));
    public static final DeferredHolder<MobEffect, MobEffect> GLOOM = BUFFS.register("gloom", () -> new utmMobEffect(MobEffectCategory.BENEFICIAL, 0x333333).addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.withDefaultNamespace("effect.gloom"), (double)-2.0F, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, MobEffect> INFUSED = BUFFS.register("infused", () -> new utmMobEffect(MobEffectCategory.BENEFICIAL, 0x550000).addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.withDefaultNamespace("effect.infuse"), (double)-2.0F, AttributeModifier.Operation.ADD_VALUE));

}
