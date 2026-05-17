package com.nadia.utm.client.renderer;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.network.chat.Component;

import java.util.List;

@SuppressWarnings("EmptyMethod")
public interface IBypassRPM extends IHaveGoggleInformation {
    default void useTooltip(List<Component> tooltip, KineticBlockEntity be) {

    }
}
