package com.nadia.utm.client.renderer;

import com.nadia.utm.util.utmLang;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface IBypassRPM extends IHaveGoggleInformation {
    default void useTooltip(List<Component> tooltip, KineticBlockEntity be) {
        utmLang.text("Generating " + Math.abs(be.getGeneratedSpeed()) + " RPM").style(ChatFormatting.WHITE).forGoggles(tooltip);
    }
}
