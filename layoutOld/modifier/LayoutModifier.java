package com.redpxnda.nucleus.widgets.layoutOld.modifier;

/**
 * They are meant to modify/contraint the widgets limits, like size, gaps etc.
 * They dont modify the final position of the widget.
 */
public interface LayoutModifier {
    Measurable wrap(Measurable child);
}