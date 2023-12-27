package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.NucleusTest;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NucleusTest.MOD_ID)
public class NucleusTestForge {
    public NucleusTestForge() {
        EventBuses.registerModEventBus(NucleusTest.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusTest.init();
    }
}
