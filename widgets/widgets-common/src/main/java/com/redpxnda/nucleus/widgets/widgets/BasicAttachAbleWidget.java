package com.redpxnda.nucleus.widgets.widgets;

import com.redpxnda.nucleus.widgets.state.UiAttachable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

/**
 * {@link UiAttachable} is meant to provide *some* idea if an element is still attached to a screen
 * this is package private because its just one feature of the full {@link NucleusWidget}
 */
@SuppressWarnings("unused")
abstract class BasicAttachAbleWidget extends BasicHirarchyWidget implements UiAttachable {
    //start attached.
    //yeah, if you create a widget and never attach it will always think its attached.
    //idk how to change that sensibly without accidental disconnects during widget initialisation
    private boolean attached = true;
    /**
     * This is a Widget build to support Children and parse the events down to them.
     * Best use in conjunction with the ParentHandledScreen as it also handles Children correct,
     * unlike the base vanilla classes.
     * If you choose to handle some Events yourself and want to support Children yourself, you need to call the correct
     * super method or handle the children yourself
     *
     * @param x      the X Position
     * @param y      the y Position
     * @param width  the width
     * @param height the height
     *               These for Params above are used to create feedback on isMouseOver() by default
     * @param title  the Title of the Widget
     */
    protected BasicAttachAbleWidget(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
    }

    /**
     * Override these if StateWidget should follow your existing
     * InteractAbleWidget lifecycle.
     */
    @Override
    public void attach() {
        attached = true;
        for (GuiEventListener listener : children) {
            if (listener instanceof UiAttachable uiAttachable) {
                uiAttachable.attach();
            }
        }
    }

    public boolean isAttached() {
        return attached;
    }


    @Override
    public void removeChild(GuiEventListener listener) {
        super.removeChild(listener);
        if (listener instanceof UiAttachable uiAttachable) {
            uiAttachable.detach();
        }
    }

    @Override
    public void detach() {
        attached = false;
        for (GuiEventListener listener : children) {
            if (listener instanceof UiAttachable uiAttachable) {
                uiAttachable.detach();
            }
        }
    }
}
