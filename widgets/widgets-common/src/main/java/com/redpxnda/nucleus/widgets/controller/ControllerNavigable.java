package com.redpxnda.nucleus.widgets.controller;

import net.minecraft.client.gui.navigation.ScreenDirection;

import java.util.List;
import java.util.Optional;

public interface ControllerNavigable {

    boolean isFocusable();

    boolean isFocused();

    void setFocused(boolean focused);

    Optional<ControllerNavigable> getNavigationParent();

    Optional<ControllerNavigable> getNeighbor(ScreenDirection direction);

    void setNeighbor(ScreenDirection direction, ControllerNavigable target);

    /**
     * Returns the children which participate in controller navigation.
     */
    default Iterable<? extends ControllerNavigable> getNavigationChildren() {
        return List.of();
    }

    /**
     * Selects the best child when entering this node from a direction.
     */
    default Optional<ControllerNavigable> getNavigationEntry(ScreenDirection from) {
        return Optional.empty();
    }

    default boolean onConfirm() {
        return false;
    }

    default boolean onBack() {
        return false;
    }
}