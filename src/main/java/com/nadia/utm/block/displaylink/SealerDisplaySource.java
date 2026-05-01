package com.nadia.utm.block.displaylink;

import com.nadia.utm.block.entity.AbstractSealerBlockEntity;
import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class SealerDisplaySource extends DisplaySource {
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats displayTargetStats) {
        List<Component> elements = new ArrayList<>();

        if (context.getSourceBlockEntity() instanceof AbstractSealerBlockEntity be) {
            be.addToGoggleTooltip(elements, true);
        }

        return elements.stream().map(Component::copy).toList();
    }
}
