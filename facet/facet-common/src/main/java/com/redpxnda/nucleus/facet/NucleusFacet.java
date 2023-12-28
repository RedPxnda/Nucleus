package com.redpxnda.nucleus.facet;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.facet.network.TrackingUpdateSyncer;
import com.redpxnda.nucleus.facet.network.clientbound.FacetSyncPacket;

public class NucleusFacet {
    public static final String MOD_ID = "nucleus_facet";
    
    public static void init() {
        System.out.println("!!!!LOADING FACET!!!!!");
        TrackingUpdateSyncer.init();
        Nucleus.registerPacket(FacetSyncPacket.class, FacetSyncPacket::new);
    }
}
