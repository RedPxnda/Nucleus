package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.item.ItemStackReference;
import net.minecraft.inventory.StackReference;

@SuppressWarnings("unused")
public class SlotAccessReference extends Reference<StackReference> {
    static { Reference.register(SlotAccessReference.class); }

    public SlotAccessReference(StackReference instance) {
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
