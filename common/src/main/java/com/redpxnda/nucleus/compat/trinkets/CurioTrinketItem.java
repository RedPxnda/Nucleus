package com.redpxnda.nucleus.compat.trinkets;

import com.redpxnda.nucleus.impl.TrinketItemCreator;
import net.minecraft.world.item.Item;

public class CurioTrinketItem extends Item implements CurioTrinket {
    public CurioTrinketItem(Properties properties) {
        super(properties);
        TrinketItemCreator.registerCurioTrinket(this, this);
    }

    /*@Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack, LivingEntity entity, int slotIndex, UUID uuid) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        map.put(Attributes.ARMOR, new AttributeModifier(uuid, "test", 5, AttributeModifier.Operation.ADDITION));
        return map;
    }*/
}
