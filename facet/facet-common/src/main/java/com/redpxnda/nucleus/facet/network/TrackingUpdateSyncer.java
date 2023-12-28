package com.redpxnda.nucleus.facet.network;

import com.redpxnda.nucleus.event.EntityEvents;
import com.redpxnda.nucleus.facet.entity.EntityFacet;
import com.redpxnda.nucleus.facet.FacetKey;

import java.util.ArrayList;
import java.util.List;

public final class TrackingUpdateSyncer {
    private TrackingUpdateSyncer() {}

    final static List<FacetKey<? extends EntityFacet<?>>> facetsToSync = new ArrayList<>();

    public static void init() {
        EntityEvents.TRACKING_CHANGE.register((stage, entity, player) -> {
            if (stage != EntityEvents.TrackingStage.STARTED) return;
            facetsToSync.forEach(key -> {
                EntityFacet<?> facet = key.get(entity);
                if (facet != null) facet.sendToClient(entity, player);
            });
        });
    }

    public static void register(FacetKey<? extends EntityFacet<?>> key) {
        facetsToSync.add(key);
    }
}
