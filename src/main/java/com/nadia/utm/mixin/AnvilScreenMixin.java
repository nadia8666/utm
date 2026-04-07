package com.nadia.utm.mixin;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.world.entity.player.Abilities;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("SameReturnValue")
@Mixin(value = AnvilScreen.class, remap = false)
public class AnvilScreenMixin {
    @Redirect(method = "renderLabels", at= @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Abilities;instabuild:Z", opcode = Opcodes.GETFIELD))
    public boolean utm$overrideTooExpensive(Abilities instance) {
        return true;
    }
}
