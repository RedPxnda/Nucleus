package com.redpxnda.nucleus.widgets.layout.layouter;

import com.redpxnda.nucleus.widgets.layout.*;

/** Factory methods for reusable child layout modifiers. */
public final class LayoutModifiers {
    private LayoutModifiers() {}

    /** Limits the size reported by a child and the size given to it during placement. */
    public static LayoutModifier maxSize(int maxWidth, int maxHeight) {
        if (maxWidth < 0 || maxHeight < 0) {
            throw new IllegalArgumentException("Maximum dimensions cannot be negative");
        }

        return child -> new Measurable() {
            @Override
            public LayoutSize measure(LayoutConstraints constraints) {
                LayoutConstraints limited = new LayoutConstraints(
                    Math.min(constraints.maxWidth(), maxWidth),
                    Math.min(constraints.maxHeight(), maxHeight)
                );
                return constraints.constrain(child.measure(limited));
            }

            @Override
            public void place(LayoutBounds bounds) {
                child.place(new LayoutBounds(
                    bounds.x(), bounds.y(),
                    Math.min(bounds.width(), maxWidth),
                    Math.min(bounds.height(), maxHeight)
                ));
            }
        };
    }

    /** Adds external space around a child. Order is left, top, right, bottom. */
    public static LayoutModifier margin(int left, int top, int right, int bottom) {
        validateInsets(left, top, right, bottom);
        int horizontal = saturatedAdd(left, right);
        int vertical = saturatedAdd(top, bottom);

        return child -> new Measurable() {
            @Override
            public LayoutSize measure(LayoutConstraints constraints) {
                LayoutSize inner = child.measure(constraints.inset(horizontal, vertical));
                return constraints.constrain(new LayoutSize(
                    saturatedAdd(inner.width(), horizontal),
                    saturatedAdd(inner.height(), vertical)
                ));
            }

            @Override
            public void place(LayoutBounds bounds) {
                child.place(new LayoutBounds(
                    bounds.x() + left,
                    bounds.y() + top,
                    Math.max(0, bounds.width() - horizontal),
                    Math.max(0, bounds.height() - vertical)
                ));
            }
        };
    }

    private static void validateInsets(int left, int top, int right, int bottom) {
        if (left < 0 || top < 0 || right < 0 || bottom < 0) {
            throw new IllegalArgumentException("Insets cannot be negative");
        }
    }

    static int saturatedAdd(int a, int b) {
        long sum = (long) a + b;
        return sum > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) sum;
    }
}
