package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.NucleusFacet;
import net.fabricmc.api.ModInitializer;

public class NucleusFacetFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusFacet.init();
    }
}
