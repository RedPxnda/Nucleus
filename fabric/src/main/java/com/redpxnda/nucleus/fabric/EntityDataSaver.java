package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.capability.EntityCapability;

import java.util.Map;

public interface EntityDataSaver {
    Map<String, EntityCapability<?>> getCapabilities();
}
