package com.redpxnda.nucleus.impl.forge;

import com.redpxnda.nucleus.compat.CuriousTrinketItem;
import com.redpxnda.nucleus.forge.compat.CuriousTrinketItemImpl;
import net.minecraft.world.item.Item;

public class TrinketItemCreatorImpl {
    public static Item createTrinketOrCurio(CuriousTrinketItem mock) {
        return new CuriousTrinketItemImpl(mock, mock.properties);
    }
}
