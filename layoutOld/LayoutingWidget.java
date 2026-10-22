package com.redpxnda.nucleus.widgets.layoutOld;

import com.redpxnda.nucleus.widgets.layoutOld.layouter.Layouter;
import com.redpxnda.nucleus.widgets.layoutOld.modifier.LayoutParams;
import com.redpxnda.nucleus.widgets.widgets.NucleusWidget;

public interface LayoutingWidget {
    void setLayouter(Layouter layouter);

    void addLayoutedChild(
            NucleusWidget child,
            LayoutParams params
    );

    default void addLayoutedChild(NucleusWidget child) {
        addLayoutedChild(child, LayoutParams.defaults());
    }

    void requestLayout();
}