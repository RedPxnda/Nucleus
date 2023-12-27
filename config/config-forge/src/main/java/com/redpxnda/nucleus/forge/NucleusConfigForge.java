package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.NucleusConfig;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NucleusConfig.MOD_ID)
public class NucleusConfigForge {
    public NucleusConfigForge() {
        EventBuses.registerModEventBus(NucleusConfig.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusConfig.init();
    }
}
