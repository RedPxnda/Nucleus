package com.redpxnda.nucleus.widgets.widgets;

import com.redpxnda.nucleus.widgets.layout.*;
import com.redpxnda.nucleus.widgets.layout.layouter.Layouter;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * NucleusWidget base class that owns layout-managed children.
 * The external/root owner still supplies the bounds of a root layout widget.
 */
public abstract class BasicLayoutingWidget extends BasicAttachAbleWidget implements LayoutingWidget {
    private final List<LayoutChild> layoutChildren = new ArrayList<>();
    private Layouter layouter;
    private BasicLayoutingWidget layoutParent;
    private boolean layingOut;
    private boolean layoutDirty;

    protected BasicLayoutingWidget(
        int x,
        int y,
        int width,
        int height,
        Component title,
        Layouter layouter
    ) {
        super(x, y, width, height, title);
        this.layouter = Objects.requireNonNull(layouter, "layouter");
    }

    @Override
    public void setLayouter(Layouter layouter) {
        this.layouter = Objects.requireNonNull(layouter, "layouter");
        requestLayout();
    }

    @Override
    public void addLayoutedChild(NucleusWidget child, LayoutParams params) {
        Objects.requireNonNull(child, "child");
        Objects.requireNonNull(params, "params");

        if (children().contains(child)) {
            throw new IllegalArgumentException("Widget is already a child of this container");
        }
        if (child == this) {
            throw new IllegalArgumentException("A layout widget cannot contain itself");
        }

        layoutChildren.add(new LayoutChild(child, params));
        super.addChild(child); // Keeps Nucleus attachment and parent behavior.

        if (child instanceof BasicLayoutingWidget nested) {
            nested.layoutParent = this;
        }
        requestLayout();
    }

    @Override
    public void removeLayoutedChild(NucleusWidget child) {
        removeChild(child);
    }

    @Override
    public void removeChild(GuiEventListener child) {
        boolean removed = layoutChildren.removeIf(entry -> entry.widget() == child);
        if (child instanceof BasicLayoutingWidget nested && nested.layoutParent == this) {
            nested.layoutParent = null;
        }
        super.removeChild(child);
        if (removed) requestLayout();
    }

    @Override
    public List<LayoutChild> getLayoutedChildren() {
        return List.copyOf(layoutChildren);
    }

    @Override
    public LayoutSize measure(LayoutConstraints constraints) {
        return layouter.measure(this, getLayoutedChildren(), constraints);
    }

    /** Nucleus calls this whenever this widget moves or changes size. */
    @Override
    public void sizeUpdate() {
        super.sizeUpdate();
        if (layouter != null) requestLayout();
    }

    @Override
    public final void requestLayout() {
        layoutDirty = true;
        if (layingOut || layouter == null) return;

        int passes = 0;
        do {
            if (++passes > 32) {
                throw new IllegalStateException(
                    "Layout did not converge after 32 passes. Check for cyclic layout updates."
                );
            }

            layoutDirty = false;
            layingOut = true;
            try {
                layouter.arrange(
                    this,
                    getLayoutedChildren(),
                    new LayoutBounds(getX(), getY(), getWidth(), getHeight())
                );
            } finally {
                layingOut = false;
            }
        } while (layoutDirty);

        if (layoutParent != null) layoutParent.requestLayout();
    }
}
