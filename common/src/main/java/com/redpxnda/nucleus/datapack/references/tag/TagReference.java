package com.redpxnda.nucleus.datapack.references.tag;

import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.nbt.Tag;

@SuppressWarnings("unused")
public class TagReference<T extends Tag> extends Reference<T> {
    static { Reference.register(TagReference.class); }

    public TagReference(T instance) {
        super(instance);
    }

    // Generated from Tag::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from Tag::getId
    public byte getId() {
        return instance.getId();
    }

    // Generated from Tag::copy
    public TagReference<?> copy() {
        return new TagReference<>(instance.copy());
    }

    // Generated from Tag::getAsString
    public String getAsString() {
        return instance.getAsString();
    }
}
