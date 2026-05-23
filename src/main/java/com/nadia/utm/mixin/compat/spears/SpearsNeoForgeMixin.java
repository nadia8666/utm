package com.nadia.utm.mixin.compat.spears;

import com.notunanancyowen.spears.neoforge.SpearsNeoForge;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SpearsNeoForge.class, remap = false)
public class SpearsNeoForgeMixin {
    @Redirect(method = "lambda$registerStuff$9", at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private static boolean utm$sheldonCopper(ModList instance, String modTarget) {
        return true;
    }
}
