package com.redpxnda.nucleus.widgets.layout.layouter;

import com.redpxnda.nucleus.widgets.layout.*;

import java.util.List;
import java.util.Objects;

/** Decorates another layouter with inner padding. Order is left, top, right, bottom. */
public final class PaddingLayouter implements Layouter {
    private final int left;
    private final int top;
    private final int right;
    private final int bottom;
    private final int horizontal;
    private final int vertical;
    private final Layouter delegate;

    public PaddingLayouter(int left, int top, int right, int bottom, Layouter delegate) {
        if (left < 0 || top < 0 || right < 0 || bottom < 0) {
            throw new IllegalArgumentException("Padding cannot be negative");
        }
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.horizontal = LayoutModifiers.saturatedAdd(left, right);
        this.vertical = LayoutModifiers.saturatedAdd(top, bottom);
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override
    public LayoutSize measure(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutConstraints constraints
    ) {
        LayoutSize content = delegate.measure(
            parent, children, constraints.inset(horizontal, vertical)
        );
        return constraints.constrain(new LayoutSize(
            LayoutModifiers.saturatedAdd(content.width(), horizontal),
            LayoutModifiers.saturatedAdd(content.height(), vertical)
        ));
    }

    @Override
    public void arrange(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutBounds bounds
    ) {
        delegate.arrange(parent, children, new LayoutBounds(
            bounds.x() + left,
            bounds.y() + top,
            Math.max(0, bounds.width() - horizontal),
            Math.max(0, bounds.height() - vertical)
        ));
    }
}
