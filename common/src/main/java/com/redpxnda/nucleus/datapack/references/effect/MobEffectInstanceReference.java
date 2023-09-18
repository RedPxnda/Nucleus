package com.redpxnda.nucleus.datapack.references.effect;

import com.redpxnda.nucleus.datapack.references.Reference;
import com.redpxnda.nucleus.datapack.references.storage.ResourceLocationReference;
import com.redpxnda.nucleus.datapack.references.tag.CompoundTagReference;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;

@SuppressWarnings("unused")
public class MobEffectInstanceReference extends Reference<StatusEffectInstance> {
    static { Reference.register(MobEffectInstanceReference.class); }

    public MobEffectInstanceReference(StatusEffectInstance instance) {
        super(instance);
    }

    // Generated from MobEffectInstance::equals
    public boolean equals(Object param0) {
        return instance.equals(param0);
    }

    // Generated from MobEffectInstance::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from MobEffectInstance::compareTo
    public int compareTo(MobEffectInstanceReference param0) {
        return instance.compareTo(param0.instance);
    }

    // Generated from MobEffectInstance::update
    public boolean update(MobEffectInstanceReference param0) {
        return instance.upgrade(param0.instance);
    }

    // Generated from MobEffectInstance::save
    public CompoundTagReference save(CompoundTagReference param0) {
        return new CompoundTagReference(instance.writeNbt(param0.instance));
    }

    // Generated from MobEffectInstance::getDuration
    public int getDuration() {
        return instance.getDuration();
    }

    // Generated from MobEffectInstance::showIcon
    public boolean showIcon() {
        return instance.shouldShowIcon();
    }

    public ResourceLocationReference getEffect() {
        return new ResourceLocationReference(Registries.STATUS_EFFECT.getId(instance.getEffectType()));
    }

    // Generated from MobEffectInstance::isVisible
    public boolean isVisible() {
        return instance.shouldShowParticles();
    }

    // Generated from MobEffectInstance::getAmplifier
    public int getAmplifier() {
        return instance.getAmplifier();
    }

    // Generated from MobEffectInstance::isAmbient
    public boolean isAmbient() {
        return instance.isAmbient();
    }

    // Generated from MobEffectInstance::getDescriptionId
    public String getDescriptionId() {
        return instance.getTranslationKey();
    }
}
