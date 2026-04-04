package com.nadia.utm.mixin;

import com.nadia.utm.util.OxyUtil;
import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = BacktankBlockEntity.class, remap = false)
public class BacktankBlockEntityMixin implements IHaveGoggleInformation {
    @Shadow private int capacityEnchantLevel;
    @Unique
    private boolean utm$noOxygen = false;
    @Unique
    private boolean utm$isFilling = false;
    @Unique
    private double utm$fillSpeed = 0.0;
    @Unique
    private int utm$maxAir = 0;
    @Unique
    private int utm$collectedRate = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void utm$getMax(CallbackInfo ci) {
        this.utm$maxAir = BacktankUtil.maxAir(this.capacityEnchantLevel);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/equipment/armor/BacktankUtil;maxAir(I)I"))
    private int utm$updateOxygenInfo(int enchantLevel) {
        BacktankBlockEntity tank = (BacktankBlockEntity) (Object) this;
        Level level = tank.getLevel();
        if (level == null) return utm$maxAir;

        this.utm$noOxygen = !OxyUtil.hasOxygen(level);

        if (this.utm$noOxygen) {
            double strength = OxyUtil.getCollectionStrength(level, tank.getBlockPos());
            this.utm$isFilling = strength > 0;
            this.utm$fillSpeed = strength;
        } else {
            this.utm$isFilling = true;
        }

        return utm$maxAir;
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/equipment/armor/BacktankBlockEntity;airLevel:I", opcode = 180))
    private int utm$oxygenParticles(BacktankBlockEntity instance) {
        if (this.utm$isFilling) {
            return instance.airLevel;
        } else {
            return this.utm$maxAir;
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(III)I", ordinal = 0))
    private int utm$checkAir(int value, int min, int max) {
        int realTarget = Mth.clamp(value, min, max);

        if (this.utm$noOxygen) {
            int actualValue = this.utm$isFilling ? Math.max(Mth.floor(realTarget * this.utm$fillSpeed), 0) : 0;
            this.utm$collectedRate = actualValue;
            return actualValue;
        }

        return realTarget;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void utm$syncOxygen(CallbackInfo ci) {
        BacktankBlockEntity tank = (BacktankBlockEntity) (Object) this;
        Level level = tank.getLevel();
        if (level == null || level.isClientSide()) return;

        if (level.getGameTime() % 20 == 0)
            tank.sendData();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        BacktankBlockEntity tank = (BacktankBlockEntity) (Object) this;
        Level level = tank.getLevel();
        if (level == null) return false;

        float abs = Math.abs(tank.getSpeed());
        utm$checkAir(((int) abs - 100) / 20, 1, 5);

        int counted = (int) OxyUtil.getCollectionStrength(level, tank.getBlockPos(), null, true);
        String efficiency = String.format("%.1f", this.utm$fillSpeed * 100);

        utmLang.text("Collection Info:").style(ChatFormatting.WHITE).forGoggles(tooltip);
        utmLang.text(tank.getAirLevel() + "/" + utm$maxAir).style(ChatFormatting.AQUA).space().add(utmLang.text("Oxygen").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        utmLang.text(String.valueOf(this.utm$collectedRate * 20)).style(ChatFormatting.AQUA).space().add(utmLang.text("Oxygen per second").style(ChatFormatting.GRAY)).forGoggles(tooltip);
        utmLang.text(efficiency + "% Efficiency").style(
                this.utm$fillSpeed > .85 ? ChatFormatting.GOLD :
                        this.utm$fillSpeed > .5 ? ChatFormatting.GREEN :
                                this.utm$fillSpeed > .2 ? ChatFormatting.RED :
                                        ChatFormatting.DARK_RED
        ).forGoggles(tooltip);
        utmLang.text(counted + "/342").style(ChatFormatting.AQUA).space().add(utmLang.text("Leaves").style(ChatFormatting.GRAY)).forGoggles(tooltip);

        return true;
    }
}