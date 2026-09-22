package com.redpxnda.nucleus.widgets.state;

/**
 * meant as a lifecicle tracker to prevent State logic from keeping track of unneccesary stuff
 */
@SuppressWarnings("unused")
public interface UiAttachable {

    void attach();

    void detach();

    boolean isAttached();
}