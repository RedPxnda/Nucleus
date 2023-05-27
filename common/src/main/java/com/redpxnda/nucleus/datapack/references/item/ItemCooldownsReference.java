package com.redpxnda.nucleus.datapack.references.item;

import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.world.item.ItemCooldowns;

@SuppressWarnings("unused")
public class ItemCooldownsReference extends Reference<ItemCooldowns> {
    static { Reference.register(ItemCooldownsReference.class); }

    public ItemCooldownsReference(ItemCooldowns instance) {
        super(instance);
    }

    // Generated from ItemCooldowns::addCooldown
    public void addCooldown(ItemReference<?> param0, int param1) {
        instance.addCooldown(param0.instance, param1);
    }

    // Generated from ItemCooldowns::getCooldownPercent
    public float getCooldownPercent(ItemReference<?> param0, float param1) {
        return instance.getCooldownPercent(param0.instance, param1);
    }

    // Generated from ItemCooldowns::isOnCooldown
    public boolean isOnCooldown(ItemReference<?> param0) {
        return instance.isOnCooldown(param0.instance);
    }

    // Generated from ItemCooldowns::removeCooldown
    public void removeCooldown(ItemReference<?> param0) {
        instance.removeCooldown(param0.instance);
    }
}
