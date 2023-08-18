package com.redpxnda.nucleus.capability;

import com.redpxnda.nucleus.event.EntityEvents;
import com.redpxnda.nucleus.impl.EntityDataManager;
import com.redpxnda.nucleus.pose.ServerPoseCapability;

import java.util.ArrayList;
import java.util.List;

public final class TrackingUpdateSyncer {
    private TrackingUpdateSyncer() {}

    final static List<Class<? extends SyncedEntityCapability<?>>> capsToSync = new ArrayList<>();

    public static void init() {
        register(ServerPoseCapability.class);

        EntityEvents.TRACKING_CHANGE.register((stage, entity, player) -> {
            if (stage != EntityEvents.TrackingStage.STARTED) return;
            capsToSync.forEach(cls -> {
                SyncedEntityCapability<?> cap = EntityDataManager.getCapability(entity, cls);
                if (cap != null) cap.sendToClient(entity, player);
            });
        });
    }

    public static void register(Class<? extends SyncedEntityCapability<?>> cls) {
        capsToSync.add(cls);
    }
}
