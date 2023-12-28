package com.redpxnda.nucleus.pose.forge;

import com.redpxnda.nucleus.pose.NucleusPose;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NucleusPose.MOD_ID)
public class NucleusPoseForge {
    public NucleusPoseForge() {
        EventBuses.registerModEventBus(NucleusPose.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusPose.init();
    }
}
