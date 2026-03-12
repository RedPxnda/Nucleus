package com.redpxnda.nucleus.trinket.forge;

import com.redpxnda.nucleus.trinket.NucleusTrinket;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

@Mod(NucleusTrinket.MOD_ID)
public class NucleusTrinketForge {
    public NucleusTrinketForge() {
        IItemHandlerModifiable modifiable;
        NucleusTrinket.init();
    }
}
