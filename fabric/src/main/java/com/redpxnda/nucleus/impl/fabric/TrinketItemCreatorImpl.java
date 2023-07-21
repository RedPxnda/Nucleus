package com.redpxnda.nucleus.impl.fabric;

import com.redpxnda.nucleus.compat.CuriousTrinketItem;
import com.redpxnda.nucleus.fabric.compat.CuriousTrinketItemImpl;
import net.minecraft.world.item.Item;

public class TrinketItemCreatorImpl {
    public static Item createTrinketOrCurio(CuriousTrinketItem mock) {
        return new CuriousTrinketItemImpl(mock, mock.properties);
    }
}
