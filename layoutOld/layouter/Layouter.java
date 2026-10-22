package com.redpxnda.nucleus.widgets.layoutOld.layouter;

import com.redpxnda.nucleus.widgets.layoutOld.*;

import java.util.List;

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