package com.redpxnda.nucleus.capability.item;

import java.util.Map;

public interface ItemStackDataSaver {
    Map<String, ItemStackCapability<?>> getCapabilities();
}
