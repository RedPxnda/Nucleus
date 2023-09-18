package com.redpxnda.nucleus.datapack.references.item;

import com.mojang.datafixers.util.Pair;
import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.effect.MobEffectInstanceReference;
import java.util.List;
import net.minecraft.item.FoodComponent;

@SuppressWarnings("unused")
public class FoodPropertiesReference extends Reference<FoodComponent> {
    static { Reference.register(FoodPropertiesReference.class); }

    public FoodPropertiesReference(FoodComponent instance) {
        super(instance);
    }

    // Generated from FoodProperties::getSaturationModifier
    public float getSaturationModifier() {
        return instance.getSaturationModifier();
    }

    // Generated from FoodProperties::isMeat
    public boolean isMeat() {
        return instance.isMeat();
    }

    // Generated from FoodProperties::getNutrition
    public int getNutrition() {
        return instance.getHunger();
    }

    // Generated from FoodProperties::getEffects
    public List<Pair<MobEffectInstanceReference, Float>> getEffects() {
        return instance.getStatusEffects().stream().map(pair -> Pair.of(new MobEffectInstanceReference(pair.getFirst()), pair.getSecond())).toList();
    }

    // Generated from FoodProperties::canAlwaysEat
    public boolean canAlwaysEat() {
        return instance.isAlwaysEdible();
    }

    // Generated from FoodProperties::isFastFood
    public boolean isFastFood() {
        return instance.isSnack();
    }

}
