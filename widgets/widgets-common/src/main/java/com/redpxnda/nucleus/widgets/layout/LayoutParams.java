package com.redpxnda.nucleus.widgets.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Immutable, per-child layout configuration. */
public final class LayoutParams {
    private final List<LayoutModifier> modifiers;
    private final float flexGrow;

    private LayoutParams(List<LayoutModifier> modifiers, float flexGrow) {
        if (!Float.isFinite(flexGrow) || flexGrow < 0) {
            throw new IllegalArgumentException("flexGrow must be finite and non-negative");
        }
        this.modifiers = List.copyOf(modifiers);
        this.flexGrow = flexGrow;
    }

    public static LayoutParams defaults() {
        return new LayoutParams(List.of(), 0);
    }

    public LayoutParams then(LayoutModifier modifier) {
        Objects.requireNonNull(modifier, "modifier");
        List<LayoutModifier> copy = new ArrayList<>(modifiers);
        copy.add(modifier);
        return new LayoutParams(copy, flexGrow);
    }

    /** Requests a share of positive leftover space in a RowLayouter or ColumnLayouter. */
    public LayoutParams flexToSize(float weight) {
        return new LayoutParams(modifiers, weight);
    }

    public List<LayoutModifier> modifiers() {
        return modifiers;
    }

    public float flexGrow() {
        return flexGrow;
    }
}
