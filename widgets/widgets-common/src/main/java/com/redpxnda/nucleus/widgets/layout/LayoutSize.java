package com.redpxnda.nucleus.widgets.layout;

public record LayoutSize(int width, int height) {
    public LayoutSize {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Layout sizes cannot be negative");
        }
    }
}
