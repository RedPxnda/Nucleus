package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.Nucleus;
import net.fabricmc.api.ModInitializer;

public class NucleusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Nucleus.init();
    }
}
