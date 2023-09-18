package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.Statics;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("unused")
public class Vec3Reference extends Reference<Vec3d> implements Position {
    static { Reference.register(Vec3Reference.class); }

    public Vec3Reference(Vec3d instance) {
        super(instance);
    }

    // Generated from Vec3::add
    public Vec3Reference add(Vec3Reference param0) {
        instance.add(param0.instance);
        return this;
    }

    // Generated from Vec3::add
    public Vec3Reference add(double param0, double param1, double param2) {
        instance.add(param0, param1, param2);
        return this;
    }

    // Generated from Vec3::get
    public double get(Statics.Axes param0) {
        return instance.getComponentAlongAxis(param0.instance);
    }

    // Generated from Vec3::equals
    public boolean equals(Object param0) {
        return instance.equals(param0);
    }

    // Generated from Vec3::length
    public double length() {
        return instance.length();
    }

    // Generated from Vec3::scale
    public Vec3Reference scale(double param0) {
        instance.multiply(param0);
        return this;
    }

    // Generated from Vec3::x
    public double getX() {
        return instance.getX();
    }

    // Generated from Vec3::dot
    public double dot(Vec3Reference param0) {
        return instance.dotProduct(param0.instance);
    }

    // Generated from Vec3::z
    public double getZ() {
        return instance.getZ();
    }

    // Generated from Vec3::normalize
    public Vec3Reference normalize() {
        instance.normalize();
        return this;
    }

    // Generated from Vec3::reverse
    public Vec3Reference reverse() {
        instance.negate();
        return this;
    }

    // Generated from Vec3::y
    public double getY() {
        return instance.getY();
    }

    // Generated from Vec3::multiply
    public Vec3Reference multiply(Vec3Reference param0) {
        instance.multiply(param0.instance);
        return this;
    }

    // Generated from Vec3::multiply
    public Vec3Reference multiply(double param0, double param1, double param2) {
        instance.multiply(param0, param1, param2);
        return this;
    }

    // Generated from Vec3::with
    public Vec3Reference with(Statics.Axes param0, double param1) {
        instance.withAxis(param0.instance, param1);
        return this;
    }

    // Generated from Vec3::subtract
    public Vec3Reference subtract(Vec3Reference param0) {
        instance.subtract(param0.instance);
        return this;
    }

    // Generated from Vec3::subtract
    public Vec3Reference subtract(double param0, double param1, double param2) {
        instance.subtract(param0, param1, param2);
        return this;
    }

    // Generated from Vec3::relative
    public Vec3Reference relative(Statics.Directions param0, double param1) {
        instance.offset(param0.instance, param1);
        return this;
    }

    // Generated from Vec3::horizontalDistanceSqr
    public double horizontalDistanceSqr() {
        return instance.horizontalLengthSquared();
    }

    // Generated from Vec3::vectorTo
    public Vec3Reference vectorTo(Vec3Reference param0) {
        instance.relativize(param0.instance);
        return this;
    }

    // Generated from Vec3::distanceToSqr
    public double distanceToSqr(Vec3Reference param0) {
        return instance.squaredDistanceTo(param0.instance);
    }

    // Generated from Vec3::distanceToSqr
    public double distanceToSqr(double param0, double param1, double param2) {
        return instance.squaredDistanceTo(param0, param1, param2);
    }

    // Generated from Vec3::zRot
    public Vec3Reference zRot(float param0) {
        instance.rotateZ(param0);
        return this;
    }

    // Generated from Vec3::distanceTo
    public double distanceTo(Vec3Reference param0) {
        return instance.distanceTo(param0.instance);
    }

    // Generated from Vec3::cross
    public Vec3Reference cross(Vec3Reference param0) {
        instance.crossProduct(param0.instance);
        return this;
    }

    // Generated from Vec3::closerThan
    public boolean closerThan(Position param0, double param1) {
        return instance.isInRange(param0, param1);
    }

    // Generated from Vec3::lengthSqr
    public double lengthSqr() {
        return instance.lengthSquared();
    }

    // Generated from Vec3::lerp
    public Vec3Reference lerp(Vec3Reference param0, double param1) {
        instance.lerp(param0.instance, param1);
        return this;
    }

    // Generated from Vec3::xRot
    public Vec3Reference xRot(float param0) {
        instance.rotateX(param0);
        return this;
    }

    // Generated from Vec3::horizontalDistance
    public double horizontalDistance() {
        return instance.horizontalLength();
    }

    // Generated from Vec3::align
//    public Vec3Reference align(EnumSet param0) {
//        return new Vec3Reference(instance.align(param0));
//    }

    // Generated from Vec3::yRot
    public Vec3Reference yRot(float param0) {
        instance.rotateY(param0);
        return this;
    }
}
