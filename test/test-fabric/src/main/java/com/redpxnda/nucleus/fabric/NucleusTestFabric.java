package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.NucleusTest;
import net.fabricmc.api.ModInitializer;

public class NucleusTestFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusTest.init();
    }
}
