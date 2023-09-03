package com.redpxnda.nucleus.resolving.wrappers;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public interface AABBWrapping {
    static AABB getAsAABB(AABBWrapping wrapping) {
        return (AABB) wrapping;
    }
    
    /**
     * Returns the average length of the edges of the bounding box.
     */
    @WrapperMethod(alias = "size")
    default double nucleusWrapper$getSize() {
        return getAsAABB(this).getSize();
    }

    @WrapperMethod(alias = "x_size")
    default double nucleusWrapper$getXsize() {
        return getAsAABB(this).getXsize();
    }

    @WrapperMethod(alias = "y_size")
    default double nucleusWrapper$getYsize() {
        return getAsAABB(this).getYsize();
    }

    @WrapperMethod(alias = "z_size")
    default double nucleusWrapper$getZsize() {
        return getAsAABB(this).getZsize();
    }

    @WrapperMethod(alias = "has_nan")
    default boolean nucleusWrapper$hasNaN() {
        return getAsAABB(this).hasNaN();
    }

    @WrapperMethod(alias = "center")
    default Vec3 nucleusWrapper$getCenter() {
        return getAsAABB(this).getCenter();
    }
}
