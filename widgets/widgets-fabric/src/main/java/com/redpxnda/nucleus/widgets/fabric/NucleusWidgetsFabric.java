package com.redpxnda.nucleus.widgets.fabric;

import com.redpxnda.nucleus.widgets.NucleusWidgets;
import net.fabricmc.api.ModInitializer;

public class NucleusWidgetsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusWidgets.init();
    }
}
