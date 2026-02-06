package com.nadia.utm.mixin.compat;

import com.nadia.utm.gui.compat.ObituaryDropButton;
import de.maxhenkel.gravestone.corelib.death.Death;
import de.maxhenkel.gravestone.gui.ObituaryScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ObituaryScreen.class, remap = false)
public abstract class ObituaryScreenMixin extends Screen {
    protected ObituaryScreenMixin(Component title) {
        super(title);
    }

    @Accessor(value = "death", remap = false)
    public abstract Death utm$getDeath();

    @Inject(
            method = "init",
            at = @At("RETURN"),
            remap = false
    )
    private void utm$addDropButton(CallbackInfo ci) {
        var death = utm$getDeath();
        addRenderableWidget(new ObituaryDropButton((width - 160)/2 +10, 20+118, 25, 25, Component.empty(), death.getBlockPos()));
    }
}
