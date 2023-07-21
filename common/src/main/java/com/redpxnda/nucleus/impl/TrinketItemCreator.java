package com.redpxnda.nucleus.impl;

import com.redpxnda.nucleus.compat.CuriousTrinketItem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.Item;

public class TrinketItemCreator {
    @ExpectPlatform
    public static Item createTrinketOrCurio(CuriousTrinketItem mock) {
        throw new AssertionError();
    }
}
