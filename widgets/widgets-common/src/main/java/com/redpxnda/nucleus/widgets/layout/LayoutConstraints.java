package com.redpxnda.nucleus.widgets.layout;

public record LayoutConstraints(int maxWidth, int maxHeight) {
    public static final int UNBOUNDED = Integer.MAX_VALUE;

    public LayoutConstraints {
        if (maxWidth < 0 || maxHeight < 0) {
            throw new IllegalArgumentException("Layout constraints cannot be negative");
        }
    }

    public static LayoutConstraints unbounded() {
        return new LayoutConstraints(UNBOUNDED, UNBOUNDED);
    }

    public LayoutSize constrain(LayoutSize size) {
        return new LayoutSize(
            Math.min(size.width(), maxWidth),
            Math.min(size.height(), maxHeight)
        );
    }

    public LayoutConstraints inset(int horizontal, int vertical) {
        return new LayoutConstraints(
            Math.max(0, maxWidth - horizontal),
            Math.max(0, maxHeight - vertical)
        );
    }
}
