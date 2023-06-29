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

    // todo update to new mc system
    /*// Generated from DamageSource::isExplosion
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
    }*/

    // Generated from DamageSource::getDirectEntity
    public EntityReference<?> getDirectEntity() {
        return new EntityReference<>(instance.getDirectEntity());
    }

    // Generated from DamageSource::getEntity
    public EntityReference<?> getEntity() {
        return new EntityReference<>(instance.getEntity());
    }

    // Generated from DamageSource::getSourcePosition
    public Vec3Reference getSourcePosition() {
        return new Vec3Reference(instance.getSourcePosition());
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
