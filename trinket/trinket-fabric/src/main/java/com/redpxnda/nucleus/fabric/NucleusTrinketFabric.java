package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.NucleusTrinket;
import net.fabricmc.api.ModInitializer;

public class NucleusTrinketFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusTrinket.init();
    }
}
