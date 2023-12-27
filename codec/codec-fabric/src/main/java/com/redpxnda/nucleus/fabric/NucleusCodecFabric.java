package com.redpxnda.nucleus.fabric;

import com.redpxnda.nucleus.NucleusCodec;
import net.fabricmc.api.ModInitializer;

public class NucleusCodecFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusCodec.init();
    }
}
