package com.redpxnda.nucleus.trinket.fabric;

import com.redpxnda.nucleus.trinket.NucleusTrinket;
import net.fabricmc.api.ModInitializer;

public class NucleusTrinketFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusTrinket.init();
    }
}
