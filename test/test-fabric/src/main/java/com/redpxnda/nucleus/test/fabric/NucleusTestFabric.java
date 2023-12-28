package com.redpxnda.nucleus.test.fabric;

import com.redpxnda.nucleus.test.NucleusTest;
import net.fabricmc.api.ModInitializer;

public class NucleusTestFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusTest.init();
    }
}
