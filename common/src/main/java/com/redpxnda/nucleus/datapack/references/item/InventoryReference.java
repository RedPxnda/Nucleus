package com.redpxnda.nucleus.datapack.references.item;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.block.BlockStateReference;
import com.redpxnda.nucleus.datapack.references.entity.PlayerReference;
import com.redpxnda.nucleus.datapack.references.storage.ComponentReference;
import com.redpxnda.nucleus.datapack.references.storage.DamageSourceReference;
import com.redpxnda.nucleus.datapack.references.tag.ListTagReference;
import net.minecraft.entity.player.PlayerInventory;

@SuppressWarnings("unused")
public class InventoryReference extends Reference<PlayerInventory> {
    static { Reference.register(InventoryReference.class); }

    public InventoryReference(PlayerInventory instance) {
        super(instance);
    }

    // Generated from Inventory::getName
    public ComponentReference<?> getName() {
        return new ComponentReference<>(instance.getName());
    }

    // Generated from Inventory::add
    public boolean add(ItemStackReference param0) {
        return instance.insertStack(param0.instance);
    }

    // Generated from Inventory::add
    public boolean add(int param0, ItemStackReference param1) {
        return instance.insertStack(param0, param1.instance);
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
        return new ListTagReference(instance.writeNbt(param0.instance));
    }

    // Generated from Inventory::replaceWith
    public void replaceWith(InventoryReference param0) {
        instance.clone(param0.instance);
    }

    // Generated from Inventory::getItem
    public ItemStackReference getItem(int param0) {
        return new ItemStackReference(instance.getStack(param0));
    }

    // Generated from Inventory::findSlotMatchingUnusedItem
    public int findSlotMatchingUnusedItem(ItemStackReference param0) {
        return instance.indexOf(param0.instance);
    }

    // Generated from Inventory::getSlotWithRemainingSpace
    public int getSlotWithRemainingSpace(ItemStackReference param0) {
        return instance.getOccupiedSlotWithRoomForStack(param0.instance);
    }

    // Generated from Inventory::getSuitableHotbarSlot
    public int getSuitableHotbarSlot() {
        return instance.getSwappableHotbarSlot();
    }

    // Generated from Inventory::findSlotMatchingItem
    public int findSlotMatchingItem(ItemStackReference param0) {
        return instance.getSlotWithStack(param0.instance);
    }

    // Generated from Inventory::getTimesChanged
    public int getTimesChanged() {
        return instance.getChangeCount();
    }

    // Generated from Inventory::clearContent
    public void clearContent() {
        instance.clear();
    }

    // Generated from Inventory::dropAll
    public void dropAll() {
        instance.dropAll();
    }

    // Generated from Inventory::setChanged
    public void setChanged() {
        instance.markDirty();
    }

    // Generated from Inventory::stillValid
    public boolean stillValid(PlayerReference param0) {
        return instance.canPlayerUse(param0.instance);
    }

    // Generated from Inventory::removeFromSelected
    public ItemStackReference removeFromSelected(boolean param0) {
        return new ItemStackReference(instance.dropSelectedItem(param0));
    }

    // Generated from Inventory::getFreeSlot
    public int getFreeSlot() {
        return instance.getEmptySlot();
    }

    // Generated from Inventory::setPickedItem
    public void setPickedItem(ItemStackReference param0) {
        instance.addPickBlock(param0.instance);
    }

    // Generated from Inventory::swapPaint
    public void swapPaint(double param0) {
        instance.scrollInHotbar(param0);
    }

    // Generated from Inventory::getSelected
    public ItemStackReference getSelected() {
        return new ItemStackReference(instance.getMainHandStack());
    }

    // Generated from Inventory::pickSlot
    public void pickSlot(int param0) {
        instance.swapSlotWithHotbar(param0);
    }

    // Generated from Inventory::setItem
    public void setItem(int param0, ItemStackReference param1) {
        instance.setStack(param0, param1.instance);
    }

    // Generated from Inventory::getArmor
    public ItemStackReference getArmor(int param0) {
        return new ItemStackReference(instance.getArmorStack(param0));
    }

    // Generated from Inventory::getDestroySpeed
    public float getDestroySpeed(BlockStateReference param0) {
        return instance.getBlockBreakingSpeed(param0.instance);
    }

    // Generated from Inventory::removeItem
    public void removeItem(ItemStackReference param0) {
        instance.removeOne(param0.instance);
    }

    // Generated from Inventory::removeItem
    public ItemStackReference removeItem(int param0, int param1) {
        return new ItemStackReference(instance.removeStack(param0, param1));
    }

    // Generated from Inventory::hurtArmor
    public void hurtArmor(DamageSourceReference param0, float param1, int[] param2) {
        instance.damageArmor(param0.instance, param1, param2);
    }

    // Generated from Inventory::getContainerSize
    public int getContainerSize() {
        return instance.size();
    }

    // Generated from Inventory::removeItemNoUpdate
    public ItemStackReference removeItemNoUpdate(int param0) {
        return new ItemStackReference(instance.removeStack(param0));
    }

    // Generated from Inventory::placeItemBackInInventory
    public void placeItemBackInInventory(ItemStackReference param0, boolean param1) {
        instance.offer(param0.instance, param1);
    }

    // Generated from Inventory::placeItemBackInInventory
    public void placeItemBackInInventory(ItemStackReference param0) {
        instance.offerOrDrop(param0.instance);
    }

}
