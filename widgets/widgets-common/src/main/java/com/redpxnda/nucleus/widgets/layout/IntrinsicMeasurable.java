package com.redpxnda.nucleus.widgets.layout;

/**
 * Optional interface for widgets whose preferred size can change with their content.
 * Ordinary NucleusWidget instances use the size they had when added to a layout.
 */
public interface IntrinsicMeasurable {
    LayoutSize measureIntrinsic(LayoutConstraints constraints);
}
