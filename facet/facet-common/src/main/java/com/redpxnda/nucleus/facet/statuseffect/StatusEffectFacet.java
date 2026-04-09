package com.redpxnda.nucleus.facet.statuseffect;

import com.redpxnda.nucleus.facet.Facet;
import com.redpxnda.nucleus.facet.event.FacetAttachmentEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import com.redpxnda.nucleus.facet.FacetHolder;
import com.redpxnda.nucleus.facet.FacetRegistry;
import org.jetbrains.annotations.Nullable;

public interface StatusEffectFacet<F extends StatusEffectFacet<F, T>, T extends Tag> extends Facet<T> {
    default void onCopied(F original) {
        loadNbt(original.toNbt());
    }

    /**
     * @see MobEffectInstance#update
     */
    default void attemptUpgradeWith(@Nullable F otherFacet, MobEffectInstance otherInstance) {}

    /**
     * will only run if {@link MobEffect#applyEffectTick} is called.
     * @see MobEffect#applyEffectTick
     */
    default void applyEffectTick(LivingEntity entity, MobEffectInstance instance) {}

    /**
     * @see MobEffect#addAttributeModifiers
     */
    default void onApplied(LivingEntity entity, MobEffectInstance instance) {}

    /**
     * @see MobEffect#removeAttributeModifiers
     */
    default void onRemoved(LivingEntity entity, MobEffectInstance instance) {}

    static void setupFacets(MobEffectInstance instance) {
        FacetAttachmentEvent.FacetAttacher attacher = new FacetAttachmentEvent.FacetAttacher();
        FacetRegistry.STATUS_EFFECT_FACET_ATTACHMENT.invoker().attach(instance, attacher);
        FacetHolder holder = FacetHolder.of(instance);
        holder.setFacetsFromAttacher(attacher);
    }

    static void writeFacetsToNbt(CompoundTag root, FacetHolder holder) {
        CompoundTag facetsNbt = new CompoundTag();
        holder.getFacets().forEach((key, facet) -> {
            if(facet.shouldSave()){

                facetsNbt.put(key.id().toString(), facet.toNbt());
            }
        });
        root.put(FacetRegistry.TAG_FACETS_ID, facetsNbt);
    }
}
