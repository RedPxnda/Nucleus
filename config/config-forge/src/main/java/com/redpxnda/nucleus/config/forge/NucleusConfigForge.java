package com.redpxnda.nucleus.config.forge;

import com.redpxnda.nucleus.config.NucleusConfig;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(com.redpxnda.nucleus.config.NucleusConfig.MOD_ID)
public class NucleusConfigForge {
    public NucleusConfigForge() {
        EventBuses.registerModEventBus(com.redpxnda.nucleus.config.NucleusConfig.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusConfig.init();
    }
}
