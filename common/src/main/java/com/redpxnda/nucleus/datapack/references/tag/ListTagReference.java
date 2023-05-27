package com.redpxnda.nucleus.datapack.references.tag;

import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.nbt.ListTag;

@SuppressWarnings("unused")
public class ListTagReference extends TagReference<ListTag> {
    static { Reference.register(ListTagReference.class); }

    public ListTagReference(ListTag instance) {
        super(instance);
    }

    // Generated from ListTag::add
    public void add(int param0, TagReference<?> param1) {
        instance.add(param0, param1.instance);
    }

    // Generated from ListTag::remove
    public TagReference<?> remove(int param0) {
        return new TagReference<>(instance.remove(param0));
    }

    // Generated from ListTag::get
    public TagReference<?> get(int param0) {
        return new TagReference<>(instance.get(param0));
    }

    // Generated from ListTag::equals
    public boolean equals(Object param0) {
        return instance.equals(param0);
    }

    // Generated from ListTag::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from ListTag::getShort
    public short getShort(int param0) {
        return instance.getShort(param0);
    }

    // Generated from ListTag::getInt
    public int getInt(int param0) {
        return instance.getInt(param0);
    }

    // Generated from ListTag::getFloat
    public float getFloat(int param0) {
        return instance.getFloat(param0);
    }

    // Generated from ListTag::getDouble
    public double getDouble(int param0) {
        return instance.getDouble(param0);
    }

    // Generated from ListTag::clear
    public void clear() {
        instance.clear();
    }

    // Generated from ListTag::isEmpty
    public boolean isEmpty() {
        return instance.isEmpty();
    }

    // Generated from ListTag::size
    public int size() {
        return instance.size();
    }

    // Generated from ListTag::set
    public TagReference<?> set(int param0, TagReference<?> param1) {
        return new TagReference<>(instance.set(param0, param1.instance));
    }

    // Generated from ListTag::getId
    public byte getId() {
        return instance.getId();
    }

    // Generated from ListTag::copy
    public ListTagReference copy() {
        return new ListTagReference(instance.copy());
    }

    // Generated from ListTag::getElementType
    public byte getElementType() {
        return instance.getElementType();
    }

    // Generated from ListTag::getString
    public String getString(int param0) {
        return instance.getString(param0);
    }

    // Generated from ListTag::getCompound
    public CompoundTagReference getCompound(int param0) {
        return new CompoundTagReference(instance.getCompound(param0));
    }

    // Generated from ListTag::getLongArray
    public long[] getLongArray(int param0) {
        return instance.getLongArray(param0);
    }

    // Generated from ListTag::setTag
    public boolean setTag(int param0, TagReference<?> param1) {
        return instance.setTag(param0, param1.instance);
    }

    // Generated from ListTag::addTag
    public boolean addTag(int param0, TagReference<?> param1) {
        return instance.addTag(param0, param1.instance);
    }

    // Generated from ListTag::getIntArray
    public int[] getIntArray(int param0) {
        return instance.getIntArray(param0);
    }

    // Generated from ListTag::getList
    public ListTagReference getList(int param0) {
        return new ListTagReference(instance.getList(param0));
    }

}
