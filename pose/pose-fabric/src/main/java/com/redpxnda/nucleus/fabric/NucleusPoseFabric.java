package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.NucleusPose;
import net.fabricmc.api.ModInitializer;

public class NucleusPoseFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusPose.init();
    }
}
