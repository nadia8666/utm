package com.nadia.utm.mixin;

import com.nadia.utm.util.TickUtil;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void utm$onClientTick(CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;

        TickUtil.TARGETS.computeIfPresent(level, (ignored, tasks) -> {
            tasks.removeIf((task) -> {
                boolean toRemove = task.tick() >= level.getGameTime();

                if (toRemove)
                    task.runnable().run();

                return toRemove;
            });

            return tasks;
        });
    }
}
