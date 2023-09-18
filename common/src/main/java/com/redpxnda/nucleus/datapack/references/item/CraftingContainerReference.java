package com.redpxnda.nucleus.datapack.references.item;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import net.minecraft.inventory.RecipeInputInventory;

@SuppressWarnings("unused")
public class CraftingContainerReference extends Reference<RecipeInputInventory> {
    static { Reference.register(CraftingContainerReference.class); }

    public CraftingContainerReference(RecipeInputInventory instance) {
        super(instance);
    }

    // Generated from CraftingContainer::isEmpty
    public boolean isEmpty() {
        return instance.isEmpty();
    }

    // Generated from CraftingContainer::getItem
    public ItemStackReference getItem(int param0) {
        return new ItemStackReference(instance.getStack(param0));
    }

    // Generated from CraftingContainer::setItem
    public void setItem(int param0, ItemStackReference param1) {
        instance.setStack(param0, param1.instance);
    }

    // Generated from CraftingContainer::setChanged
    public void setChanged() {
        instance.markDirty();
    }

    // Generated from CraftingContainer::getContainerSize
    public int getContainerSize() {
        return instance.size();
    }

    // Generated from CraftingContainer::stillValid
    public boolean stillValid(PlayerReference param0) {
        return instance.canPlayerUse(param0.instance);
    }

    // Generated from CraftingContainer::removeItemNoUpdate
    public ItemStackReference removeItemNoUpdate(int param0) {
        return new ItemStackReference(instance.removeStack(param0));
    }

    // Generated from CraftingContainer::removeItem
    public ItemStackReference removeItem(int param0, int param1) {
        return new ItemStackReference(instance.removeStack(param0, param1));
    }

    // Generated from CraftingContainer::clearContent
    public void clearContent() {
        instance.clear();
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
