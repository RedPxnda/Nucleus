package com.redpxnda.nucleus.widgets.layout;

/** A node that can report a preferred size and be assigned final bounds. */
public interface Measurable {
    LayoutSize measure(LayoutConstraints constraints);

    void place(LayoutBounds bounds);
}
