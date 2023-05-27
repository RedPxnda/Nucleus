package com.redpxnda.nucleus.datapack.references.storage;

import com.redpxnda.nucleus.datapack.references.entity.EntityReference;
import com.redpxnda.nucleus.datapack.references.entity.LivingEntityReference;
import com.redpxnda.nucleus.datapack.references.Reference;
import net.minecraft.world.damagesource.DamageSource;

@SuppressWarnings("unused")
public class DamageSourceReference extends Reference<DamageSource> {
    static { Reference.register(DamageSourceReference.class); }

    public DamageSourceReference(DamageSource instance) {
        super(instance);
    }

    // Generated from DamageSource::toString
    public String toString() {
        return instance.toString();
    }

    // Generated from DamageSource::isExplosion
    public boolean isExplosion() {
        return instance.isExplosion();
    }

    // Generated from DamageSource::isProjectile
    public boolean isProjectile() {
        return instance.isProjectile();
    }

    // Generated from DamageSource::isMagic
    public boolean isMagic() {
        return instance.isMagic();
    }

    // Generated from DamageSource::isFall
    public boolean isFall() {
        return instance.isFall();
    }

    // Generated from DamageSource::setExplosion
    public DamageSourceReference setExplosion() {
        instance.setExplosion();
        return this;
    }

    // Generated from DamageSource::setProjectile
    public DamageSourceReference setProjectile() {
        instance.setProjectile();
        return this;
    }

    // Generated from DamageSource::setMagic
    public DamageSourceReference setMagic() {
        instance.setMagic();
        return this;
    }

    // Generated from DamageSource::getDirectEntity
    public EntityReference<?> getDirectEntity() {
        return new EntityReference<>(instance.getDirectEntity());
    }

    // Generated from DamageSource::getEntity
    public EntityReference<?> getEntity() {
        return new EntityReference<>(instance.getEntity());
    }

    // Generated from DamageSource::isBypassArmor
    public boolean isBypassArmor() {
        return instance.isBypassArmor();
    }

    // Generated from DamageSource::setNoAggro
    public DamageSourceReference setNoAggro() {
        instance.setNoAggro();
        return this;
    }

    // Generated from DamageSource::setIsFall
    public DamageSourceReference setIsFall() {
        instance.setIsFall();
        return this;
    }

    // Generated from DamageSource::isNoAggro
    public boolean isNoAggro() {
        return instance.isNoAggro();
    }

    // Generated from DamageSource::isFire
    public boolean isFire() {
        return instance.isFire();
    }

    // Generated from DamageSource::isBypassInvul
    public boolean isBypassInvul() {
        return instance.isBypassInvul();
    }

    // Generated from DamageSource::isCreativePlayer
    public boolean isCreativePlayer() {
        return instance.isCreativePlayer();
    }

    // Generated from DamageSource::getMsgId
    public String getMsgId() {
        return instance.getMsgId();
    }

    // Generated from DamageSource::getFoodExhaustion
    public float getFoodExhaustion() {
        return instance.getFoodExhaustion();
    }

    // Generated from DamageSource::isBypassMagic
    public boolean isBypassMagic() {
        return instance.isBypassMagic();
    }

    // Generated from DamageSource::isDamageHelmet
    public boolean isDamageHelmet() {
        return instance.isDamageHelmet();
    }

    // Generated from DamageSource::getSourcePosition
    public Vec3Reference getSourcePosition() {
        return new Vec3Reference(instance.getSourcePosition());
    }

    // Generated from DamageSource::setScalesWithDifficulty
    public DamageSourceReference setScalesWithDifficulty() {
        instance.setScalesWithDifficulty();
        return this;
    }

    // Generated from DamageSource::isBypassEnchantments
    public boolean isBypassEnchantments() {
        return instance.isBypassEnchantments();
    }

    // Generated from DamageSource::getLocalizedDeathMessage
    public ComponentReference<?> getLocalizedDeathMessage(LivingEntityReference<?> param0) {
        return new ComponentReference<>(instance.getLocalizedDeathMessage(param0.instance));
    }

    // Generated from DamageSource::scalesWithDifficulty
    public boolean scalesWithDifficulty() {
        return instance.scalesWithDifficulty();
    }

}
