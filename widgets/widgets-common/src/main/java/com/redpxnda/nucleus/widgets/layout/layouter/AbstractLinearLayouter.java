package com.redpxnda.nucleus.widgets.layout.layouter;

import com.redpxnda.nucleus.widgets.layout.*;

import java.util.ArrayList;
import java.util.List;

/** Shared implementation for row and column layouts. */
abstract class AbstractLinearLayouter implements Layouter {
    private final int gap;

    protected AbstractLinearLayouter(int gap) {
        if (gap < 0) throw new IllegalArgumentException("gap cannot be negative");
        this.gap = gap;
    }

    protected abstract boolean horizontal();

    @Override
    public LayoutSize measure(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutConstraints constraints
    ) {
        int main = 0;
        int cross = 0;

        for (LayoutChild child : children) {
            LayoutSize size = child.measurable().measure(constraints);
            main = LayoutModifiers.saturatedAdd(main, horizontal() ? size.width() : size.height());
            cross = Math.max(cross, horizontal() ? size.height() : size.width());
        }

        if (children.size() > 1) {
            main = LayoutModifiers.saturatedAdd(
                main,
                saturatedMultiply(gap, children.size() - 1)
            );
        }

        LayoutSize desired = horizontal()
            ? new LayoutSize(main, cross)
            : new LayoutSize(cross, main);
        return constraints.constrain(desired);
    }

    @Override
    public void arrange(
        LayoutingWidget parent,
        List<LayoutChild> children,
        LayoutBounds bounds
    ) {
        if (children.isEmpty()) return;

        boolean horizontal = horizontal();
        int availableMain = horizontal ? bounds.width() : bounds.height();
        LayoutConstraints constraints = new LayoutConstraints(bounds.width(), bounds.height());
        List<Measurable> nodes = new ArrayList<>(children.size());
        int[] mainSizes = new int[children.size()];
        int[] crossSizes = new int[children.size()];
        long baseMain = (long) gap * (children.size() - 1);
        double totalFlex = 0;

        for (int i = 0; i < children.size(); i++) {
            LayoutChild entry = children.get(i);
            Measurable node = entry.measurable();
            nodes.add(node);

            LayoutSize size = node.measure(constraints);
            mainSizes[i] = horizontal ? size.width() : size.height();
            crossSizes[i] = horizontal ? size.height() : size.width();
            baseMain += mainSizes[i];
            totalFlex += entry.params().flexGrow();
        }

        // Flex only grows children when space remains. This intentionally does not shrink overflow.
        int extra = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, availableMain - baseMain));
        if (extra > 0 && totalFlex > 0) {
            int used = 0;
            for (int i = 0; i < children.size(); i++) {
                float weight = children.get(i).params().flexGrow();
                if (weight > 0) {
                    int share = (int) Math.floor(extra * (double) weight / totalFlex);
                    mainSizes[i] = LayoutModifiers.saturatedAdd(mainSizes[i], share);
                    used += share;
                }
            }

            // Distribute rounding remainder so the flex items consume all leftover pixels.
            int remainder = extra - used;
            for (int i = 0; i < children.size() && remainder > 0; i++) {
                if (children.get(i).params().flexGrow() > 0) {
                    mainSizes[i]++;
                    remainder--;
                }
            }
        }

        int cursor = horizontal ? bounds.x() : bounds.y();
        for (int i = 0; i < children.size(); i++) {
            int mainSize = mainSizes[i];
            int crossSize = crossSizes[i];
            LayoutBounds childBounds = horizontal
                ? new LayoutBounds(cursor, bounds.y(), mainSize, Math.min(bounds.height(), crossSize))
                : new LayoutBounds(bounds.x(), cursor, Math.min(bounds.width(), crossSize), mainSize);
            nodes.get(i).place(childBounds);
            cursor += mainSize;
            if (i + 1 < children.size()) cursor += gap;
        }
    }

    private static int saturatedMultiply(int value, int count) {
        long product = (long) value * count;
        return product > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) product;
    }
}
