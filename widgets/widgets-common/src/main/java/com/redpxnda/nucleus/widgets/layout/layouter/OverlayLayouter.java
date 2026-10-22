package com.redpxnda.nucleus.widgets.layout.layouter;

import com.redpxnda.nucleus.widgets.layout.*;

import java.util.List;

/** Measures to the largest child and places all children over the same parent rectangle. */
public final class OverlayLayouter implements Layouter {
    @Override
    public LayoutSize measure(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutConstraints constraints
    ) {
        int width = 0;
        int height = 0;
        for (LayoutChild child : children) {
            LayoutSize size = child.measurable().measure(constraints);
            width = Math.max(width, size.width());
            height = Math.max(height, size.height());
        }
        return constraints.constrain(new LayoutSize(width, height));
    }

    @Override
    public void arrange(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutBounds bounds
    ) {
        for (LayoutChild child : children) {
            child.measurable().place(bounds);
        }
    }
}
