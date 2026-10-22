package com.redpxnda.nucleus.widgets.layout.layouter;

import com.redpxnda.nucleus.widgets.layout.*;

import java.util.List;

/** A container layout algorithm. Measurement must not change widget bounds. */
public interface Layouter {
    LayoutSize measure(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutConstraints constraints
    );

    void arrange(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutBounds bounds
    );
}
