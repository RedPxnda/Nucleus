package com.redpxnda.nucleus.widgets.controlify;

import com.redpxnda.nucleus.widgets.controller.ControllerNavigable;
import com.redpxnda.nucleus.widgets.controller.ControllerScreen;
import com.redpxnda.nucleus.widgets.widgets.NucleusScreen;
import dev.isxander.controlify.screenop.ScreenProcessor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenDirection;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Controlify integration for Nucleus controller-aware screens.
 */
public final class NucleusScreenProcessor extends ScreenProcessor<NucleusScreen<?>> {

    private final ControllerScreen nucleusScreen;

    public NucleusScreenProcessor(ControllerScreen screen) {
        super((NucleusScreen<?>) screen);
        this.nucleusScreen = screen;
    }

    @Override
    protected @Nullable Supplier<Boolean> createScreenNavigationFunc(
            ScreenDirection direction
    ) {
        ControllerNavigable current = getFocusedNavigable().orElse(null);

        if (current == null) {
            return super.createScreenNavigationFunc(direction);
        }

        Optional<ControllerNavigable> target =
                findNavigationTarget(current, direction);

        if (target.isEmpty() || target.get() == current) {
            return super.createScreenNavigationFunc(direction);
        }

        if (!(target.get() instanceof GuiEventListener listener)) {
            return super.createScreenNavigationFunc(direction);
        }

        return () -> {
            ((NucleusScreen<?>) nucleusScreen).setFocused(listener);
            return true;
        };
    }

    /**
     * Returns the currently focused Nucleus navigable.
     */
    private Optional<ControllerNavigable> getFocusedNavigable() {
        GuiEventListener focused = screen.getFocused();

        return Optional.ofNullable(
                focused instanceof ControllerNavigable navigable
                        ? navigable
                        : null
        );
    }

    /**
     * Finds the target by walking up the navigation hierarchy until an
     * explicit neighbor is found, then resolves that node to a focusable leaf.
     */
    private Optional<ControllerNavigable> findNavigationTarget(
            ControllerNavigable current,
            ScreenDirection direction
    ) {
        Set<ControllerNavigable> visited =
                Collections.newSetFromMap(new IdentityHashMap<>());

        ControllerNavigable node = current;

        while (node != null && visited.add(node)) {
            Optional<ControllerNavigable> neighbor =
                    node.getNeighbor(direction);

            if (neighbor.isPresent()) {
                return resolveEntry(
                        neighbor.get(),
                        direction,
                        visited
                );
            }

            node = node.getNavigationParent().orElse(null);
        }

        return Optional.empty();
    }

    /**
     * Resolves a navigation container to the focusable widget that should
     * receive focus.
     */
    private Optional<ControllerNavigable> resolveEntry(
            ControllerNavigable node,
            ScreenDirection direction,
            Set<ControllerNavigable> visited
    ) {
        if (!visited.add(node)) {
            return Optional.empty();
        }

        if (node.isFocusable()) {
            return Optional.of(node);
        }

        Optional<ControllerNavigable> entry =
                node.getNavigationEntry(direction.getOpposite());

        if (entry.isPresent()) {
            return resolveEntry(entry.get(), direction, visited);
        }

        for (ControllerNavigable child : node.getNavigationChildren()) {
            Optional<ControllerNavigable> target =
                    resolveEntry(child, direction, visited);

            if (target.isPresent()) {
                return target;
            }
        }

        return Optional.empty();
    }

}