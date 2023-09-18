package com.redpxnda.nucleus.datapack.references.item;

import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.entity.player.ItemCooldownManager;

@SuppressWarnings("unused")
public class ItemCooldownsReference extends Reference<ItemCooldownManager> {
    static { Reference.register(ItemCooldownsReference.class); }

    public ItemCooldownsReference(ItemCooldownManager instance) {
        super(instance);
    }

    // Generated from ItemCooldowns::addCooldown
    public void addCooldown(ItemReference<?> param0, int param1) {
        instance.set(param0.instance, param1);
    }

    // Generated from ItemCooldowns::getCooldownPercent
    public float getCooldownPercent(ItemReference<?> param0, float param1) {
        return instance.getCooldownProgress(param0.instance, param1);
    }

    // Generated from ItemCooldowns::isOnCooldown
    public boolean isOnCooldown(ItemReference<?> param0) {
        return instance.isCoolingDown(param0.instance);
    }

    // Generated from ItemCooldowns::removeCooldown
    public void removeCooldown(ItemReference<?> param0) {
        instance.remove(param0.instance);
    }
}
