package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.entity.LivingEntityReference;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

@SuppressWarnings("unused")
public class TargetingConditionsReference extends Reference<TargetingConditions> {
    static { Reference.register(TargetingConditionsReference.class); }

    public TargetingConditionsReference(TargetingConditions instance) {
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
        instance.range(param0);
        return this;
    }

    // Generated from TargetingConditions::ignoreInvisibilityTesting
    public TargetingConditionsReference ignoreInvisibilityTesting() {
        instance.ignoreInvisibilityTesting();
        return this;
    }

    // Generated from TargetingConditions::ignoreLineOfSight
    public TargetingConditionsReference ignoreLineOfSight() {
        instance.ignoreLineOfSight();
        return this;
    }
}
