package com.redpxnda.nucleus.widgets.world;

import com.redpxnda.nucleus.widgets.widgets.NucleusWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface WorldWidget {

    default void renderInWorld(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        if (this instanceof NucleusWidget widget) {
            for (GuiEventListener listener : widget.children()) {
                if (listener instanceof WorldWidget worldWidget) {
                    worldWidget.renderInWorld(drawContext, mouseX, mouseY, delta);
                }
            }
        }
    }
}