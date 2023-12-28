package com.redpxnda.nucleus.facet.fabric;

import com.redpxnda.nucleus.facet.NucleusFacet;
import net.fabricmc.api.ModInitializer;

public class NucleusFacetFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusFacet.init();
    }
}
