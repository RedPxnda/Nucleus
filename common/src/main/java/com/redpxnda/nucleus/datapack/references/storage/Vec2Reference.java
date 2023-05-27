package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.world.phys.Vec2;

@SuppressWarnings("unused")
public class Vec2Reference extends Reference<Vec2> {
    static { Reference.register(Vec2Reference.class); }

    public Vec2Reference(Vec2 instance) {
        super(instance);
    }

    // Generated from Vec2::add
    public Vec2Reference add(float param0) {
        instance.add(param0);
        return this;
    }

    // Generated from Vec2::add
    public Vec2Reference add(Vec2Reference param0) {
        instance.add(param0.instance);
        return this;
    }

    // Generated from Vec2::equals
    public boolean equals(Vec2Reference param0) {
        return instance.equals(param0.instance);
    }

    // Generated from Vec2::length
    public float length() {
        return instance.length();
    }

    // Generated from Vec2::scale
    public Vec2Reference scale(float param0) {
        instance.scale(param0);
        return this;
    }

    // Generated from Vec2::dot
    public float dot(Vec2Reference param0) {
        return instance.dot(param0.instance);
    }

    // Generated from Vec2::normalized
    public Vec2Reference normalized() {
        instance.normalized();
        return this;
    }

    // Generated from Vec2::negated
    public Vec2Reference negated() {
        instance.negated();
        return this;
    }

    // Generated from Vec2::distanceToSqr
    public float distanceToSqr(Vec2Reference param0) {
        return instance.distanceToSqr(param0.instance);
    }

    // Generated from Vec2::lengthSquared
    public float lengthSquared() {
        return instance.lengthSquared();
    }
}
