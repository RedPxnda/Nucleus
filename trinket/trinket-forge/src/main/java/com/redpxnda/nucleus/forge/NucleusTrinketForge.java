package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.NucleusTrinket;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NucleusTrinket.MOD_ID)
public class NucleusTrinketForge {
    public NucleusTrinketForge() {
        EventBuses.registerModEventBus(NucleusTrinket.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusTrinket.init();
    }
}
