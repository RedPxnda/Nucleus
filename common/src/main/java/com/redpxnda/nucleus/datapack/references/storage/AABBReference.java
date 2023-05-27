package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.block.BlockPosReference;
import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.Statics;
import net.minecraft.world.phys.AABB;

import java.util.Optional;

@SuppressWarnings("unused")
public class AABBReference extends Reference<AABB> {
    static { Reference.register(AABBReference.class); }

    public AABBReference(AABB instance) {
        super(instance);
    }

    // Generated from AABB::equals
    public boolean equals(Object param0) {
        return instance.equals(param0);
    }

    // Generated from AABB::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from AABB::min
    public double min(Statics.Axes param0) {
        return instance.min(param0.instance);
    }

    // Generated from AABB::max
    public double max(Statics.Axes param0) {
        return instance.max(param0.instance);
    }

    // Generated from AABB::inflate
    public AABBReference inflate(double param0) {
        return new AABBReference(instance.inflate(param0));
    }

    // Generated from AABB::inflate
    public AABBReference inflate(double param0, double param1, double param2) {
        return new AABBReference(instance.inflate(param0, param1, param2));
    }

    // Generated from AABB::contains
    public boolean contains(double param0, double param1, double param2) {
        return instance.contains(param0, param1, param2);
    }

    // Generated from AABB::contains
    public boolean contains(Vec3Reference param0) {
        return instance.contains(param0.instance);
    }

    // Generated from AABB::getSize
    public double getSize() {
        return instance.getSize();
    }

    // Generated from AABB::move
    public AABBReference move(double param0, double param1, double param2) {
        return new AABBReference(instance.move(param0, param1, param2));
    }

    // Generated from AABB::move
    public AABBReference move(BlockPosReference param0) {
        return new AABBReference(instance.move(param0.instance));
    }

    // Generated from AABB::move
    public AABBReference move(Vec3Reference param0) {
        return new AABBReference(instance.move(param0.instance));
    }

    // Generated from AABB::hasNaN
    public boolean hasNaN() {
        return instance.hasNaN();
    }

    // Generated from AABB::getCenter
    public Vec3Reference getCenter() {
        return new Vec3Reference(instance.getCenter());
    }

    // Generated from AABB::deflate
    public AABBReference deflate(double param0, double param1, double param2) {
        return new AABBReference(instance.deflate(param0, param1, param2));
    }

    // Generated from AABB::deflate
    public AABBReference deflate(double param0) {
        return new AABBReference(instance.deflate(param0));
    }

    // Generated from AABB::intersects
    public boolean intersects(Vec3Reference param0, Vec3Reference param1) {
        return instance.intersects(param0.instance, param1.instance);
    }

    // Generated from AABB::intersects
    public boolean intersects(double param0, double param1, double param2, double param3, double param4, double param5) {
        return instance.intersects(param0, param1, param2, param3, param4, param5);
    }

    // Generated from AABB::intersects
    public boolean intersects(AABBReference param0) {
        return instance.intersects(param0.instance);
    }

    // Generated from AABB::getYsize
    public double getYsize() {
        return instance.getYsize();
    }

    // Generated from AABB::minmax
    public AABBReference minmax(AABBReference param0) {
        return new AABBReference(instance.minmax(param0.instance));
    }

    // Generated from AABB::getZsize
    public double getZsize() {
        return instance.getZsize();
    }

    // Generated from AABB::setMaxY
    public AABBReference setMaxY(double param0) {
        return new AABBReference(instance.setMaxY(param0));
    }

    // Generated from AABB::setMinY
    public AABBReference setMinY(double param0) {
        return new AABBReference(instance.setMinY(param0));
    }

    // Generated from AABB::clip
    public Optional<Vec3Reference> clip(Vec3Reference param0, Vec3Reference param1) {
        return instance.clip(param0.instance, param1.instance).map(Vec3Reference::new);
    }

    // Generated from AABB::setMaxZ
    public AABBReference setMaxZ(double param0) {
        return new AABBReference(instance.setMaxZ(param0));
    }

    // Generated from AABB::setMinX
    public AABBReference setMinX(double param0) {
        return new AABBReference(instance.setMinX(param0));
    }

    // Generated from AABB::contract
    public AABBReference contract(double param0, double param1, double param2) {
        return new AABBReference(instance.contract(param0, param1, param2));
    }

    // Generated from AABB::expandTowards
    public AABBReference expandTowards(Vec3Reference param0) {
        return new AABBReference(instance.expandTowards(param0.instance));
    }

    // Generated from AABB::expandTowards
    public AABBReference expandTowards(double param0, double param1, double param2) {
        return new AABBReference(instance.expandTowards(param0, param1, param2));
    }

    // Generated from AABB::intersect
    public AABBReference intersect(AABBReference param0) {
        return new AABBReference(instance.intersect(param0.instance));
    }

    // Generated from AABB::setMinZ
    public AABBReference setMinZ(double param0) {
        return new AABBReference(instance.setMinZ(param0));
    }

    // Generated from AABB::setMaxX
    public AABBReference setMaxX(double param0) {
        return new AABBReference(instance.setMaxX(param0));
    }

    // Generated from AABB::getXsize
    public double getXsize() {
        return instance.getXsize();
    }
}
