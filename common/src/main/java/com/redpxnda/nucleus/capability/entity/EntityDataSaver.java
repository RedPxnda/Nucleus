package com.redpxnda.nucleus.capability.entity;

import com.redpxnda.nucleus.capability.entity.EntityCapability;

import java.util.Map;

public interface EntityDataSaver {
    Map<String, EntityCapability<?>> getCapabilities();
}
