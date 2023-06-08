package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.capability.EntityCapability;

import java.util.Collection;
import java.util.Map;

public interface IEntityDataSaver {
    Map<String, EntityCapability<?>> getCapabilities();
}
