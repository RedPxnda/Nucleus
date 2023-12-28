package com.redpxnda.nucleus.trinket.impl;

import com.redpxnda.nucleus.trinket.curiotrinket.CurioTrinket;
import com.redpxnda.nucleus.trinket.curiotrinket.CurioTrinketRenderer;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;

public class TrinketItemCreator {
    @ExpectPlatform
    public static void registerCurioTrinket(Item item, CurioTrinket trinket) {
        throw new AssertionError();
    }

    @ExpectPlatform
    @Environment(EnvType.CLIENT)
    public static void registerCurioTrinketRenderer(Item item, CurioTrinketRenderer renderer) {
        throw new AssertionError();
    }
}
