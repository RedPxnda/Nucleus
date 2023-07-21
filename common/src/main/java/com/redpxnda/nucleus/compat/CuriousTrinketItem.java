package com.redpxnda.nucleus.compat;

import net.minecraft.world.item.Item;

public class CuriousTrinketItem extends Item implements CuriousTrinket {
    public final Properties properties;

    public CuriousTrinketItem(Properties properties) {
        super(properties);
        this.properties = properties;
    }
}
