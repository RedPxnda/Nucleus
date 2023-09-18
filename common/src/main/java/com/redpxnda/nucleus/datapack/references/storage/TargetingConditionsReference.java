package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.entity.LivingEntityReference;
import net.minecraft.entity.ai.TargetPredicate;

@SuppressWarnings("unused")
public class TargetingConditionsReference extends Reference<TargetPredicate> {
    static { Reference.register(TargetingConditionsReference.class); }

    public TargetingConditionsReference(TargetPredicate instance) {
        super(instance);
    }

    // Generated from TargetingConditions::test
    public boolean test(LivingEntityReference<?> param0, LivingEntityReference<?> param1) {
        return instance.test(param0.instance, param1.instance);
    }

    // Generated from TargetingConditions::copy
    public TargetingConditionsReference copy() {
        instance.copy();
        return this;
    }

    // Generated from TargetingConditions::range
    public TargetingConditionsReference range(double param0) {
        instance.setBaseMaxDistance(param0);
        return this;
    }

    // Generated from TargetingConditions::ignoreInvisibilityTesting
    public TargetingConditionsReference ignoreInvisibilityTesting() {
        instance.ignoreDistanceScalingFactor();
        return this;
    }

    // Generated from TargetingConditions::ignoreLineOfSight
    public TargetingConditionsReference ignoreLineOfSight() {
        instance.ignoreVisibility();
        return this;
    }
}
