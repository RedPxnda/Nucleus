package com.redpxnda.nucleus.trinket;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NucleusTrinketClient {
    @Environment(EnvType.CLIENT)
    static final Map<Item, Supplier<TrinketRenderer>> RENDERER = new HashMap<>();

    @Environment(EnvType.CLIENT)
    static void registerCreatorClient(TrinketApiMirror creator) {
        for (var entry : RENDERER.entrySet()) {
            creator.registerCurioTrinketRenderer(entry.getKey(), entry.getValue());
        }
    }

    @Environment(EnvType.CLIENT)
    static void flushRegistrationsClient() {
        for (var entry : RENDERER.entrySet()) {
            NucleusTrinket.CREATOR.forEach(c -> c.registerCurioTrinketRenderer(entry.getKey(), entry.getValue()));
        }
    }
}
