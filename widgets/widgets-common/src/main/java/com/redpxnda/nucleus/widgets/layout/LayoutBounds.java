package com.redpxnda.nucleus.widgets.layout;

public record LayoutBounds(int x, int y, int width, int height) {
    public LayoutBounds {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Layout bounds cannot have negative dimensions");
        }
    }
}
