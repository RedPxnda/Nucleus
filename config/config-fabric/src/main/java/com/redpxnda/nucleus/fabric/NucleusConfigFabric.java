package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.NucleusConfig;
import net.fabricmc.api.ModInitializer;

public class NucleusConfigFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusConfig.init();
    }
}
