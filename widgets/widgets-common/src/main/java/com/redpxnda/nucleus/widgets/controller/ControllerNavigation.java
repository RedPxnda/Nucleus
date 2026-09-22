package com.redpxnda.nucleus.widgets.controller;

import net.minecraft.client.gui.navigation.ScreenDirection;

import java.util.Optional;

public final class ControllerNavigation {

    public static Optional<ControllerNavigable> navigate(
            ControllerNavigable current,
            ScreenDirection direction
    ) {
        ControllerNavigable node = current;

        while (node != null) {
            Optional<ControllerNavigable> neighbor = node.getNeighbor(direction);

            if (neighbor.isPresent()) {
                return resolveEntry(neighbor.get(), direction);
            }

            node = node.getNavigationParent().orElse(null);
        }

        return Optional.empty();
    }

    private static Optional<ControllerNavigable> resolveEntry(
            ControllerNavigable node,
            ScreenDirection direction
    ) {
        if (node.isFocusable()) {
            return Optional.of(node);
        }

        Optional<ControllerNavigable> entry =
                node.getNavigationEntry(direction.getOpposite());

        if (entry.isPresent()) {
            return resolveEntry(entry.get(), direction);
        }

        for (ControllerNavigable child : node.getNavigationChildren()) {
            Optional<ControllerNavigable> result =
                    resolveEntry(child, direction);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    private ControllerNavigation() {}
}