package com.redpxnda.nucleus.widgets.layout.layouter;

/** Places children from top to bottom, with an optional gap between siblings. */
public final class ColumnLayouter extends AbstractLinearLayouter {
    public ColumnLayouter() {
        this(0);
    }

    public ColumnLayouter(int gap) {
        super(gap);
    }

    @Override
    protected boolean horizontal() {
        return false;
    }
}
