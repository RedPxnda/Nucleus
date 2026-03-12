package com.redpxnda.nucleus.test;

import com.redpxnda.nucleus.trinket.NucleusTrinket;
import com.redpxnda.nucleus.trinket.Trinket;
import net.minecraft.world.item.Item;

public class TrinketItem extends Item implements Trinket {
    public TrinketItem(Properties properties) {
        super(properties);
        NucleusTrinket.register(this, this);
    }
}
