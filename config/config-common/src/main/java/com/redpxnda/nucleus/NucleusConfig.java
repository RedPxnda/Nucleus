package com.redpxnda.nucleus;

import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.network.clientbound.ConfigSyncPacket;

public class NucleusConfig {
    public static final String MOD_ID = "nucleus_config";
    
    public static void init() {
        ConfigManager.init();
        Nucleus.registerPacket(ConfigSyncPacket.class, ConfigSyncPacket::new);
    }
}
