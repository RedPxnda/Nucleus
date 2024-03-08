package com.redpxnda.nucleus.expression.fabric;

import com.redpxnda.nucleus.expression.NucleusExpression;
import net.fabricmc.api.ModInitializer;

public class NucleusExpressionFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusExpression.init();
    }
}
