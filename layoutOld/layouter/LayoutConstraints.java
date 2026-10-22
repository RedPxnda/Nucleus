package com.redpxnda.nucleus.widgets.layoutOld.layouter;

public record LayoutConstraints(
    int maxWidth,
    int maxHeight
) {
    public static final int UNBOUNDED = Integer.MAX_VALUE;

    public static LayoutConstraints unbounded() {
        return new LayoutConstraints(UNBOUNDED, UNBOUNDED);
    }
}