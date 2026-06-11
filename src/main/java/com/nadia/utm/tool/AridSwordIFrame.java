package com.nadia.utm.tool;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.events.BlockStateChangedEvent;
import com.nadia.utm.event.utmEventHost;
import com.nadia.utm.registry.item.tool.utmTools;
import com.nadia.utm.utm;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Objects;

@ForceLoad
public class AridSwordIFrame {



    static {
        utmEventHost.register(LivingDamageEvent.Pre.class, event -> lewfewrngetgrefq4iufrg(event));
    }

    private static void lewfewrngetgrefq4iufrg(LivingDamageEvent.Pre event) {
        utm.LOGGER.info(String.valueOf((event.getSource().getWeaponItem().getDisplayName())));
        if (Objects.requireNonNull(event.getSource().getWeaponItem()).is(utmTools.ARID_SWORD.get()))
            event.getContainer().setPostAttackInvulnerabilityTicks(0);
    }


}
