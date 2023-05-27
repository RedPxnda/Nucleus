package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import net.minecraft.world.entity.SlotAccess;

@SuppressWarnings("unused")
public class SlotAccessReference extends Reference<SlotAccess> {
    static { Reference.register(SlotAccessReference.class); }

    public SlotAccessReference(SlotAccess instance) {
        super(instance);
    }

    // Generated from SlotAccess::get
    public ItemStackReference get() {
        return new ItemStackReference(instance.get());
    }

    // Generated from SlotAccess::set
    public boolean set(ItemStackReference param0) {
        return instance.set(param0.instance);
    }
}
