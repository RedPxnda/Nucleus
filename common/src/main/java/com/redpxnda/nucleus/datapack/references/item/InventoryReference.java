package com.redpxnda.nucleus.datapack.references.item;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.datapack.references.storage.ComponentReference;
import com.redpxnda.nucleus.datapack.references.storage.DamageSourceReference;
import com.redpxnda.nucleus.datapack.references.tag.ListTagReference;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings("unused")
public class InventoryReference extends Reference<Inventory> {
    static { Reference.register(InventoryReference.class); }

    public InventoryReference(Inventory instance) {
        super(instance);
    }

    // Generated from Inventory::getName
    public ComponentReference<?> getName() {
        return new ComponentReference<>(instance.getName());
    }

    // Generated from Inventory::add
    public boolean add(ItemStackReference param0) {
        return instance.add(param0.instance);
    }

    // Generated from Inventory::add
    public boolean add(int param0, ItemStackReference param1) {
        return instance.add(param0, param1.instance);
    }

    // Generated from Inventory::isEmpty
    public boolean isEmpty() {
        return instance.isEmpty();
    }

//    // Generated from Inventory::contains
//    public boolean contains(TagKey param0) {
//        return instance.contains(param0);
//    }

    // Generated from Inventory::contains
    public boolean contains(ItemStackReference param0) {
        return instance.contains(param0.instance);
    }

    // Generated from Inventory::save
    public ListTagReference save(ListTagReference param0) {
        return new ListTagReference(instance.save(param0.instance));
    }

    // Generated from Inventory::replaceWith
    public void replaceWith(InventoryReference param0) {
        instance.replaceWith(param0.instance);
    }

    // Generated from Inventory::getItem
    public ItemStackReference getItem(int param0) {
        return new ItemStackReference(instance.getItem(param0));
    }

    // Generated from Inventory::findSlotMatchingUnusedItem
    public int findSlotMatchingUnusedItem(ItemStackReference param0) {
        return instance.findSlotMatchingUnusedItem(param0.instance);
    }

    // Generated from Inventory::getSlotWithRemainingSpace
    public int getSlotWithRemainingSpace(ItemStackReference param0) {
        return instance.getSlotWithRemainingSpace(param0.instance);
    }

    // Generated from Inventory::getSuitableHotbarSlot
    public int getSuitableHotbarSlot() {
        return instance.getSuitableHotbarSlot();
    }

    // Generated from Inventory::findSlotMatchingItem
    public int findSlotMatchingItem(ItemStackReference param0) {
        return instance.findSlotMatchingItem(param0.instance);
    }

    // Generated from Inventory::getTimesChanged
    public int getTimesChanged() {
        return instance.getTimesChanged();
    }

    // Generated from Inventory::clearContent
    public void clearContent() {
        instance.clearContent();
    }

    // Generated from Inventory::dropAll
    public void dropAll() {
        instance.dropAll();
    }

    // Generated from Inventory::setChanged
    public void setChanged() {
        instance.setChanged();
    }

    // Generated from Inventory::stillValid
    public boolean stillValid(PlayerReference param0) {
        return instance.stillValid(param0.instance);
    }

    // Generated from Inventory::removeFromSelected
    public ItemStackReference removeFromSelected(boolean param0) {
        return new ItemStackReference(instance.removeFromSelected(param0));
    }

    // Generated from Inventory::getFreeSlot
    public int getFreeSlot() {
        return instance.getFreeSlot();
    }

    // Generated from Inventory::setPickedItem
    public void setPickedItem(ItemStackReference param0) {
        instance.setPickedItem(param0.instance);
    }

    // Generated from Inventory::swapPaint
    public void swapPaint(double param0) {
        instance.swapPaint(param0);
    }

    // Generated from Inventory::getSelected
    public ItemStackReference getSelected() {
        return new ItemStackReference(instance.getSelected());
    }

    // Generated from Inventory::pickSlot
    public void pickSlot(int param0) {
        instance.pickSlot(param0);
    }

    // Generated from Inventory::setItem
    public void setItem(int param0, ItemStackReference param1) {
        instance.setItem(param0, param1.instance);
    }

    // Generated from Inventory::getArmor
    public ItemStackReference getArmor(int param0) {
        return new ItemStackReference(instance.getArmor(param0));
    }

    // Generated from Inventory::getDestroySpeed
    public float getDestroySpeed(BlockStateReference param0) {
        return instance.getDestroySpeed(param0.instance);
    }

    // Generated from Inventory::removeItem
    public void removeItem(ItemStackReference param0) {
        instance.removeItem(param0.instance);
    }

    // Generated from Inventory::removeItem
    public ItemStackReference removeItem(int param0, int param1) {
        return new ItemStackReference(instance.removeItem(param0, param1));
    }

    // Generated from Inventory::hurtArmor
    public void hurtArmor(DamageSourceReference param0, float param1, int[] param2) {
        instance.hurtArmor(param0.instance, param1, param2);
    }

    // Generated from Inventory::getContainerSize
    public int getContainerSize() {
        return instance.getContainerSize();
    }

    // Generated from Inventory::removeItemNoUpdate
    public ItemStackReference removeItemNoUpdate(int param0) {
        return new ItemStackReference(instance.removeItemNoUpdate(param0));
    }

    // Generated from Inventory::placeItemBackInInventory
    public void placeItemBackInInventory(ItemStackReference param0, boolean param1) {
        instance.placeItemBackInInventory(param0.instance, param1);
    }

    // Generated from Inventory::placeItemBackInInventory
    public void placeItemBackInInventory(ItemStackReference param0) {
        instance.placeItemBackInInventory(param0.instance);
    }

}
