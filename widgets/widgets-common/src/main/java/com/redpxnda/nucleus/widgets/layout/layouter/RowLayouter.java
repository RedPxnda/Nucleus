package com.redpxnda.nucleus.widgets.layout.layouter;

/** Places children from left to right, with an optional gap between siblings. */
public final class RowLayouter extends AbstractLinearLayouter {
    public RowLayouter() {
        this(0);
    }

    public RowLayouter(int gap) {
        super(gap);
    }

    @Override
    protected boolean horizontal() {
        return true;
    }
}
