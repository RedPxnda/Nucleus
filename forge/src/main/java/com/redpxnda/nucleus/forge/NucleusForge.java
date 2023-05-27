package com.redpxnda.nucleus.forge;

import dev.architectury.platform.forge.EventBuses;
import com.redpxnda.nucleus.Nucleus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Nucleus.MOD_ID)
public class NucleusForge {
    public NucleusForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Nucleus.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Nucleus.init();
    }
}
