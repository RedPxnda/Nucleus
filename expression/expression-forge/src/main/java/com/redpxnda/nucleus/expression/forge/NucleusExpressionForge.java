package com.redpxnda.nucleus.expression.forge;

import com.redpxnda.nucleus.expression.NucleusExpression;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NucleusExpression.MOD_ID)
public class NucleusExpressionForge {
    public NucleusExpressionForge() {
        EventBuses.registerModEventBus(NucleusExpression.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        NucleusExpression.init();
    }
}
