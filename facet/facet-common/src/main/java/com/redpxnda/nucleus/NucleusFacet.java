package com.redpxnda.nucleus;

import com.redpxnda.nucleus.facet.TrackingUpdateSyncer;
import com.redpxnda.nucleus.network.clientbound.FacetSyncPacket;

public class NucleusFacet {
    public static final String MOD_ID = "nucleus-facet";
    
    public static void init() {
        TrackingUpdateSyncer.init();
        Nucleus.registerPacket(FacetSyncPacket.class, FacetSyncPacket::new);
    }
}
