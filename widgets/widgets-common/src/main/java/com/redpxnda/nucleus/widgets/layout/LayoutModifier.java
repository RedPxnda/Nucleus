package com.redpxnda.nucleus.widgets.layout;

/** Wraps a child measurement/placement node to change its layout behavior. */
@FunctionalInterface
public interface LayoutModifier {
    Measurable wrap(Measurable child);
}
