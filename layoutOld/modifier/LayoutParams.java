package com.redpxnda.nucleus.widgets.layoutOld.modifier;

import java.util.List;

public record LayoutParams(
    List<LayoutModifier> modifiers,
    float flexGrow
) {
    public static LayoutParams defaults() {
        return new LayoutParams(List.of(), 0);
    }
}