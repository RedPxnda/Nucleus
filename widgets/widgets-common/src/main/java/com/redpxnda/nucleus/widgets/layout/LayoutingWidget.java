package com.redpxnda.nucleus.widgets.layout;

import com.redpxnda.nucleus.widgets.layout.layouter.Layouter;
import com.redpxnda.nucleus.widgets.widgets.NucleusWidget;

import java.util.List;

public interface LayoutingWidget {
    void setLayouter(Layouter layouter);

    void addLayoutedChild(NucleusWidget child, LayoutParams params);

    default void addLayoutedChild(NucleusWidget child) {
        addLayoutedChild(child, LayoutParams.defaults());
    }

    void removeLayoutedChild(NucleusWidget child);

    List<LayoutChild> getLayoutedChildren();

    LayoutSize measure(LayoutConstraints constraints);

    void requestLayout();
}
