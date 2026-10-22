package com.redpxnda.nucleus.widgets.layoutOld.modifier;

import com.redpxnda.nucleus.widgets.layoutOld.layouter.LayoutBounds;
import com.redpxnda.nucleus.widgets.layoutOld.layouter.LayoutConstraints;
import com.redpxnda.nucleus.widgets.layoutOld.LayoutSize;

public interface Measurable {
    LayoutSize measure(LayoutConstraints constraints);

    void place(LayoutBounds bounds);
}