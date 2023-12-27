package com.redpxnda.nucleus.forge;

import com.redpxnda.nucleus.NucleusCodec;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NucleusCodec.MOD_ID)
public class NucleusCodecForge {
    public NucleusCodecForge() {
        EventBuses.registerModEventBus(NucleusCodec.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusCodec.init();
    }
}
