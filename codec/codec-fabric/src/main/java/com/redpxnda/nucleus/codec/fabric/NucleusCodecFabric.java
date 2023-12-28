package com.redpxnda.nucleus.codec.fabric;

import com.redpxnda.nucleus.codec.NucleusCodec;
import net.fabricmc.api.ModInitializer;

public class NucleusCodecFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusCodec.init();
    }
}
