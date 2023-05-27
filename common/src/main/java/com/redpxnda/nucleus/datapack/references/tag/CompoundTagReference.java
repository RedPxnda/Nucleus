package com.redpxnda.nucleus.datapack.references.tag;

import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class CompoundTagReference extends TagReference<CompoundTag> {
    static { Reference.register(CompoundTagReference.class); }

    public CompoundTagReference(CompoundTag instance) {
        super(instance);
    }

    // Generated from CompoundTag::remove
    public void remove(String param0) {
        instance.remove(param0);
    }

    // Generated from CompoundTag::get
    public TagReference<?> get(String param0) {
        return new TagReference<>(instance.get(param0));
    }

    // Generated from CompoundTag::put
    public void put(String param0, TagReference<?> param1) {
        instance.put(param0, param1.instance);
    }

    // Generated from CompoundTag::equals
    public boolean equals(Object param0) {
        return instance.equals(param0);
    }

    // Generated from CompoundTag::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from CompoundTag::getBoolean
    public boolean getBoolean(String param0) {
        return instance.getBoolean(param0);
    }

    // Generated from CompoundTag::putBoolean
    public void putBoolean(String param0, boolean param1) {
        instance.putBoolean(param0, param1);
    }

    // Generated from CompoundTag::getByte
    public byte getByte(String param0) {
        return instance.getByte(param0);
    }

    // Generated from CompoundTag::putByte
    public void putByte(String param0, byte param1) {
        instance.putByte(param0, param1);
    }

    // Generated from CompoundTag::getShort
    public short getShort(String param0) {
        return instance.getShort(param0);
    }

    // Generated from CompoundTag::putShort
    public void putShort(String param0, short param1) {
        instance.putShort(param0, param1);
    }

    // Generated from CompoundTag::getInt
    public int getInt(String param0) {
        return instance.getInt(param0);
    }

    // Generated from CompoundTag::putInt
    public void putInt(String param0, int param1) {
        instance.putInt(param0, param1);
    }

    // Generated from CompoundTag::getLong
    public long getLong(String param0) {
        return instance.getLong(param0);
    }

    // Generated from CompoundTag::putLong
    public void putLong(String param0, long param1) {
        instance.putLong(param0, param1);
    }

    // Generated from CompoundTag::getFloat
    public float getFloat(String param0) {
        return instance.getFloat(param0);
    }

    // Generated from CompoundTag::putFloat
    public void putFloat(String param0, float param1) {
        instance.putFloat(param0, param1);
    }

    // Generated from CompoundTag::getDouble
    public double getDouble(String param0) {
        return instance.getDouble(param0);
    }

    // Generated from CompoundTag::putDouble
    public void putDouble(String param0, double param1) {
        instance.putDouble(param0, param1);
    }

    // Generated from CompoundTag::isEmpty
    public boolean isEmpty() {
        return instance.isEmpty();
    }

    // Generated from CompoundTag::size
    public int size() {
        return instance.size();
    }

    // Generated from CompoundTag::contains
    public boolean contains(String param0, int param1) {
        return instance.contains(param0, param1);
    }

    // Generated from CompoundTag::contains
    public boolean contains(String param0) {
        return instance.contains(param0);
    }

    // Generated from CompoundTag::merge
    public CompoundTagReference merge(CompoundTagReference param0) {
        instance.merge(param0.instance);
        return this;
    }

    // Generated from CompoundTag::getId
    public byte getId() {
        return instance.getId();
    }

    // Generated from CompoundTag::copy
    public CompoundTagReference copy() {
        return new CompoundTagReference(instance.copy());
    }

    // Generated from CompoundTag::putByteArray
    public void putByteArray(String param0, List<Byte> param1) {
        instance.putByteArray(param0, param1);
    }

    // Generated from CompoundTag::putByteArray
    public void putByteArray(String param0, byte[] param1) {
        instance.putByteArray(param0, param1);
    }

    // Generated from CompoundTag::getString
    public String getString(String param0) {
        return instance.getString(param0);
    }

    // Generated from CompoundTag::putUUID
    public void putUUID(String param0, UUID param1) {
        instance.putUUID(param0, param1);
    }

    // Generated from CompoundTag::putLongArray
    public void putLongArray(String param0, long[] param1) {
        instance.putLongArray(param0, param1);
    }

    // Generated from CompoundTag::putLongArray
    public void putLongArray(String param0, List<Long> param1) {
        instance.putLongArray(param0, param1);
    }

    // Generated from CompoundTag::getAllKeys
    public Set<String> getAllKeys() {
        return instance.getAllKeys();
    }

    // Generated from CompoundTag::hasUUID
    public boolean hasUUID(String param0) {
        return instance.hasUUID(param0);
    }

    // Generated from CompoundTag::getUUID
    public UUID getUUID(String param0) {
        return instance.getUUID(param0);
    }

    // Generated from CompoundTag::putString
    public void putString(String param0, String param1) {
        instance.putString(param0, param1);
    }

    // Generated from CompoundTag::putIntArray
    public void putIntArray(String param0, List<Integer> param1) {
        instance.putIntArray(param0, param1);
    }

    // Generated from CompoundTag::putIntArray
    public void putIntArray(String param0, int[] param1) {
        instance.putIntArray(param0, param1);
    }

    // Generated from CompoundTag::getTagType
    public byte getTagType(String param0) {
        return instance.getTagType(param0);
    }

    // Generated from CompoundTag::getIntArray
    public int[] getIntArray(String param0) {
        return instance.getIntArray(param0);
    }

    // Generated from CompoundTag::getList
    public ListTagReference getList(String param0, int param1) {
        return new ListTagReference(instance.getList(param0, param1));
    }

    // Generated from CompoundTag::getByteArray
    public byte[] getByteArray(String param0) {
        return instance.getByteArray(param0);
    }

    // Generated from CompoundTag::getLongArray
    public long[] getLongArray(String param0) {
        return instance.getLongArray(param0);
    }

    // Generated from CompoundTag::getCompound
    public CompoundTagReference getCompound(String param0) {
        return new CompoundTagReference(instance.getCompound(param0));
    }
}
