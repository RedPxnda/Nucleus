package com.redpxnda.nucleus.widgets.widgets;

import com.redpxnda.nucleus.widgets.controller.ControllerNavigable;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Basic controller navigation implementation for a stateful widget.
 *
 * <p>Supports focus state, hierarchical navigation, explicit directional
 * neighbors, and child navigation.</p>
 *
 * this is package private because its just one feature of the full {@link NucleusWidget}
 */
@SuppressWarnings("unused")
abstract class BasicControllerNavigableWidget extends BasicAttachAbleWidget implements ControllerNavigable {

    private boolean focused;
    private ControllerNavigable navigationParent;

    private final Map<ScreenDirection, ControllerNavigable> neighbors = new EnumMap<>(ScreenDirection.class);
    private final List<ControllerNavigable> navigationChildren = new ArrayList<>();

    protected BasicControllerNavigableWidget(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
    }

    /** Returns whether this widget can receive focus. */
    @Override
    public boolean isFocusable() {
        return true;
    }

    /** Returns whether this widget currently has focus. */
    @Override
    public boolean isFocused() {
        return focused;
    }

    /** Updates the focus state. */
    @Override
    public void setFocused(boolean focused) {
        this.focused = isFocusable() && focused;
    }

    /** Returns the controller navigation parent. */
    @Override
    public Optional<ControllerNavigable> getNavigationParent() {
        return Optional.ofNullable(navigationParent);
    }

    /** Sets the controller navigation parent. */
    public void setNavigationParent(@Nullable ControllerNavigable parent) {
        if (navigationParent == parent) {
            return;
        }

        if (navigationParent instanceof BasicControllerNavigableWidget oldParent) {
            oldParent.removeNavigationChild(this);
        }

        navigationParent = parent;

        if (parent instanceof BasicControllerNavigableWidget newParent) {
            newParent.addNavigationChild(this);
        }
    }

    /** Returns the explicit neighbor in the given direction. */
    @Override
    public Optional<ControllerNavigable> getNeighbor(ScreenDirection direction) {
        return Optional.ofNullable(neighbors.get(direction));
    }

    /**
     * Sets an explicit navigation neighbor.
     *
     * <p>Passing {@code null} removes the neighbor.</p>
     */
    @Override
    public void setNeighbor(ScreenDirection direction, @Nullable ControllerNavigable target) {
        if (target == null) {
            neighbors.remove(direction);
        } else {
            neighbors.put(direction, target);
        }
    }

    /** Returns this widget's controller-navigation children. */
    @Override
    public Iterable<? extends ControllerNavigable> getNavigationChildren() {
        return Collections.unmodifiableList(navigationChildren);
    }

    /**
     * Returns the first focusable descendant when entering this widget.
     *
     * <p>Override this for directional container-specific behavior.</p>
     */
    @Override
    public Optional<ControllerNavigable> getNavigationEntry(ScreenDirection from) {
        for (ControllerNavigable child : navigationChildren) {
            if (child.isFocusable()) {
                return Optional.of(child);
            }

            Optional<ControllerNavigable> descendant = child.getNavigationEntry(from);
            if (descendant.isPresent()) {
                return descendant;
            }
        }

        return Optional.empty();
    }

    /** Handles controller confirm input. */
    @Override
    public boolean onConfirm() {
        return false;
    }

    /** Handles controller back input. */
    @Override
    public boolean onBack() {
        return false;
    }

    /**
     * Registers a controller-navigation child.
     *
     * <p>Prefer {@link BasicHirarchyWidget#addChild} for normal hierarchy
     * management.</p>
     */
    protected void addNavigationChild(ControllerNavigable child) {
        if (child != this && !navigationChildren.contains(child)) {
            navigationChildren.add(child);
        }
    }

    /** Removes a controller-navigation child. */
    protected void removeNavigationChild(ControllerNavigable child) {
        navigationChildren.remove(child);
    }

    /**
     * Sets this widget's hierarchy parent and controller parent.
     *
     * <p>Do not call this directly for normal child management.</p>
     */
    @Override
    public void setParent(@Nullable BasicHirarchyWidget parent) {
        super.setParent(parent);

        setNavigationParent(parent instanceof ControllerNavigable navigable ? navigable : null);
    }

    /**
     * Sets this widget's screen parent and controller parent.
     *
     * <p>This is called automatically when added to a screen.</p>
     */
    public void setParent(@Nullable Screen screen) {
        super.setParent(screen);

        setNavigationParent(screen instanceof ControllerNavigable navigable ? navigable : null);
    }
}
