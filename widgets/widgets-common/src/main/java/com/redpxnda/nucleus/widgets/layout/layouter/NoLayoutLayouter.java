package com.redpxnda.nucleus.widgets.layout.layouter;

import com.redpxnda.nucleus.widgets.layout.*;
import com.redpxnda.nucleus.widgets.widgets.NucleusWidget;

import java.util.List;

/**
 * Does not modify child positions or sizes.
 * Preserves the parent's current size during measurement.
 */
public final class NoLayoutLayouter implements Layouter {

    @Override
    public LayoutSize measure(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutConstraints constraints
    ) {
        if (parent instanceof NucleusWidget widget) {
            return constraints.constrain(
                new LayoutSize(widget.getWidth(), widget.getHeight())
            );
        }

        return new LayoutSize(0, 0);
    }

    @Override
    public void arrange(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutBounds bounds
    ) {
        // Intentionally does nothing.
    }
}