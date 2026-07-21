package com.nadia.utm.tool;

import com.nadia.utm.event.ForceLoad;
import com.nadia.utm.event.utmEventHost;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@ForceLoad
public class AridSwordIFrame {
    static {
        utmEventHost.register(LivingDamageEvent.Pre.class, AridSwordIFrame::onHit);
    }

    private static void onHit(LivingDamageEvent.Pre event) {

        //doesnt work but thats ok ! NOT!
    }
}
