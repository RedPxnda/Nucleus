package com.redpxnda.nucleus.config.fabric;

import com.redpxnda.nucleus.config.NucleusConfig;
import net.fabricmc.api.ModInitializer;

public class NucleusConfigFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusConfig.init();
    }
}
