package com.redpxnda.nucleus.widgets.layout;

import com.redpxnda.nucleus.widgets.widgets.NucleusWidget;

import java.util.List;
import java.util.Objects;

/** Child widget, its layout parameters, and its initial fallback preferred size. */
public final class LayoutChild {
    private final NucleusWidget widget;
    private final LayoutParams params;
    private final LayoutSize initialSize;

    public LayoutChild(NucleusWidget widget, LayoutParams params) {
        this.widget = Objects.requireNonNull(widget, "widget");
        this.params = Objects.requireNonNull(params, "params");
        this.initialSize = new LayoutSize(widget.getWidth(), widget.getHeight());
    }

    public NucleusWidget widget() {
        return widget;
    }

    public LayoutParams params() {
        return params;
    }

    public Measurable measurable() {
        Measurable node = new Measurable() {
            @Override
            public LayoutSize measure(LayoutConstraints constraints) {
                LayoutSize preferred;
                if (widget instanceof LayoutingWidget layoutingWidget) {
                    preferred = layoutingWidget.measure(constraints);
                } else if (widget instanceof IntrinsicMeasurable intrinsicMeasurable) {
                    preferred = intrinsicMeasurable.measureIntrinsic(constraints);
                } else {
                    preferred = initialSize;
                }
                return constraints.constrain(preferred);
            }

            @Override
            public void place(LayoutBounds bounds) {
                widget.updateDimensions(bounds.x(), bounds.y(), bounds.width(), bounds.height());
            }
        };

        List<LayoutModifier> modifiers = params.modifiers();
        for (int i = modifiers.size() - 1; i >= 0; i--) {
            node = modifiers.get(i).wrap(node);
        }
        return node;
    }
}
