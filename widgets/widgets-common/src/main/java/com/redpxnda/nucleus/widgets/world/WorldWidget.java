package com.redpxnda.nucleus.widgets.world;

import com.redpxnda.nucleus.widgets.widgets.NucleusWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface WorldWidget {

    /**
     * ONLY called if attached to a {@link WorldWidgetBase} BER renderer.
     * Renders during BER pass actually in-world with fake GuiGraphics
     */
    default void renderInWorld(GuiGraphics drawContext, int mouseX, int mouseY, float delta, int light, int overlay) {
        if (this instanceof NucleusWidget widget) {
            for (GuiEventListener listener : widget.children()) {
                if (listener instanceof WorldWidget worldWidget) {
                    worldWidget.renderInWorld(drawContext, mouseX, mouseY, delta, light, overlay);
                }
            }
        }
    }
}