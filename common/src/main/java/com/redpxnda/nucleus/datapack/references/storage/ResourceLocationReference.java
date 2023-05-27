package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class ResourceLocationReference extends Reference<ResourceLocation> {
    static { Reference.register(ResourceLocationReference.class); }

    public ResourceLocationReference(ResourceLocation instance) {
        super(instance);
    }

    // Generated from ResourceLocation::equals
    public boolean equals(Object param0) {
        return instance.equals(param0);
    }

    // Generated from ResourceLocation::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from ResourceLocation::compareTo
    public int compareTo(ResourceLocationReference param0) {
        return instance.compareTo(param0.instance);
    }

    // Generated from ResourceLocation::getPath
    public String getPath() {
        return instance.getPath();
    }

    // Generated from ResourceLocation::toLanguageKey
    public String toLanguageKey(String param0) {
        return instance.toLanguageKey(param0);
    }

    // Generated from ResourceLocation::toLanguageKey
    public String toLanguageKey() {
        return instance.toLanguageKey();
    }

    // Generated from ResourceLocation::toShortLanguageKey
    public String toShortLanguageKey() {
        return instance.toShortLanguageKey();
    }

    // Generated from ResourceLocation::getNamespace
    public String getNamespace() {
        return instance.getNamespace();
    }
}
