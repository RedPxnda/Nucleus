package com.redpxnda.nucleus.pose.fabric;

import com.redpxnda.nucleus.pose.NucleusPose;
import net.fabricmc.api.ModInitializer;

public class NucleusPoseFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusPose.init();
    }
}
