package com.redpxnda.nucleus.test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.redpxnda.nucleus.trinket.CommonSlotReference;
import com.redpxnda.nucleus.trinket.NucleusTrinket;
import com.redpxnda.nucleus.trinket.Trinket;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TrinketItem extends Item implements Trinket {
    public TrinketItem(Properties properties) {
        super(properties);
        NucleusTrinket.register(this, this);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(
            CommonSlotReference ctx, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> map = ArrayListMultimap.create();
        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, 2.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return map;
    }
}
