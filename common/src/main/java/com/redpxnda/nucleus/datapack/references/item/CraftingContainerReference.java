package com.redpxnda.nucleus.datapack.references.item;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import net.minecraft.world.inventory.CraftingContainer;

@SuppressWarnings("unused")
public class CraftingContainerReference extends Reference<CraftingContainer> {
    static { Reference.register(CraftingContainerReference.class); }

    public CraftingContainerReference(CraftingContainer instance) {
        super(instance);
    }

    // Generated from CraftingContainer::isEmpty
    public boolean isEmpty() {
        return instance.isEmpty();
    }

    // Generated from CraftingContainer::getItem
    public ItemStackReference getItem(int param0) {
        return new ItemStackReference(instance.getItem(param0));
    }

    // Generated from CraftingContainer::setItem
    public void setItem(int param0, ItemStackReference param1) {
        instance.setItem(param0, param1.instance);
    }

    // Generated from CraftingContainer::setChanged
    public void setChanged() {
        instance.setChanged();
    }

    // Generated from CraftingContainer::getContainerSize
    public int getContainerSize() {
        return instance.getContainerSize();
    }

    // Generated from CraftingContainer::stillValid
    public boolean stillValid(PlayerReference param0) {
        return instance.stillValid(param0.instance);
    }

    // Generated from CraftingContainer::removeItemNoUpdate
    public ItemStackReference removeItemNoUpdate(int param0) {
        return new ItemStackReference(instance.removeItemNoUpdate(param0));
    }

    // Generated from CraftingContainer::removeItem
    public ItemStackReference removeItem(int param0, int param1) {
        return new ItemStackReference(instance.removeItem(param0, param1));
    }

    // Generated from CraftingContainer::clearContent
    public void clearContent() {
        instance.clearContent();
    }

    // Generated from CraftingContainer::getHeight
    public int getHeight() {
        return instance.getHeight();
    }

    // Generated from CraftingContainer::getWidth
    public int getWidth() {
        return instance.getWidth();
    }
}
