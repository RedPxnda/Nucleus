package com.redpxnda.nucleus.resolving.wrappers;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("unused")
public interface BoxWrapping {
    static Box getAsAABB(BoxWrapping wrapping) {
        return (Box) wrapping;
    }
    
    /**
     * Returns the average length of the edges of the bounding box.
     */
    @WrapperMethod(alias = "size")
    default double nucleusWrapper$getSize() {
        return getAsAABB(this).getAverageSideLength();
    }

    @WrapperMethod(alias = "x_size")
    default double nucleusWrapper$getXsize() {
        return getAsAABB(this).getXLength();
    }

    @WrapperMethod(alias = "y_size")
    default double nucleusWrapper$getYsize() {
        return getAsAABB(this).getYLength();
    }

    @WrapperMethod(alias = "z_size")
    default double nucleusWrapper$getZsize() {
        return getAsAABB(this).getZLength();
    }

    @WrapperMethod(alias = "has_nan")
    default boolean nucleusWrapper$hasNaN() {
        return getAsAABB(this).isNaN();
    }

    @WrapperMethod(alias = "center")
    default Vec3d nucleusWrapper$getCenter() {
        return getAsAABB(this).getCenter();
    }
}
