package com.redpxnda.nucleus.widgets.widgets;

import net.minecraft.network.chat.Component;

/**
 * Full implementation of all features.
 * includes {@link com.redpxnda.nucleus.widgets.state.UiAttachable}
 * includes {@link com.redpxnda.nucleus.widgets.state.StateSubscriber}
 * includes {@link com.redpxnda.nucleus.widgets.controller.ControllerNavigable}
 * full support for hierarchy, states, detachment support and controller support
 */
public abstract class NucleusWidget extends BasicControllerNavigableWidget {
    protected NucleusWidget(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
    }
}
