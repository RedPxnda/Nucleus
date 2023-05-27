package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.Statics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

@SuppressWarnings("unused")
public class ComponentReference<C extends Component> extends Reference<C> {
    static { Reference.register(ComponentReference.class); }

    public ComponentReference(C instance) {
        super(instance);
    }

    public Mutable mutable() {
        return (Mutable) this;
    }

    // Generated from Component::contains
    public boolean contains(ComponentReference<?> param0) {
        return instance.contains(param0.instance);
    }

    // Generated from Component::copy
    public ComponentReference<?> copy() {
        return new ComponentReference<>(instance.copy());
    }

    // Generated from Component::getString
    public String getString() {
        return instance.getString();
    }

    // Generated from Component::getString
    public String getString(int param0) {
        return instance.getString(param0);
    }

    // Generated from Component::getSiblings
    public List<ComponentReference<Component>> getSiblings() {
        return instance.getSiblings().stream().map(ComponentReference::new).toList();
    }

    public static class Mutable extends ComponentReference<MutableComponent> {
        public Mutable(MutableComponent instance) {
            super(instance);
        }

        public Mutable append(ComponentReference<?> component) {
            instance.append(component.instance);
            return this;
        }

        public Mutable withStyle(Statics.ChatFormattings chatFormatting) {
            instance.withStyle(chatFormatting.instance);
            return this;
        }
    }
}
