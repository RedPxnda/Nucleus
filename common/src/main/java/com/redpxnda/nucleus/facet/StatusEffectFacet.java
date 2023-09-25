package com.redpxnda.nucleus.facet;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

public interface StatusEffectFacet<F extends StatusEffectFacet<F, T>, T extends NbtElement> extends Facet<T> {
    default void onCopied(F original) {
        loadNbt(original.toNbt());
    }

    /**
     * @see StatusEffectInstance#upgrade
     */
    default void attemptUpgradeWith(@Nullable F otherFacet, StatusEffectInstance otherInstance) {}

    /**
     * will only run if {@link StatusEffect#applyUpdateEffect} is called.
     * @see StatusEffect#applyUpdateEffect
     */
    default void applyEffectUpdate(LivingEntity entity, StatusEffectInstance instance) {}

    /**
     * @see StatusEffect#onApplied
     */
    default void onApplied(LivingEntity entity, StatusEffectInstance instance) {}

    /**
     * @see StatusEffect#onRemoved
     */
    default void onRemoved(LivingEntity entity, StatusEffectInstance instance) {}

    static void setupFacets(StatusEffectInstance instance) {
        FacetAttachmentEvent.FacetAttacher attacher = new FacetAttachmentEvent.FacetAttacher();
        FacetRegistry.STATUS_EFFECT_FACET_ATTACHMENT.invoker().attach(instance, attacher);
        FacetHolder holder = FacetHolder.of(instance);
        holder.setFacetsFromAttacher(attacher);
    }

    static void writeFacetsToNbt(NbtCompound root, FacetHolder holder) {
        NbtCompound facetsNbt = new NbtCompound();
        holder.getFacets().forEach((key, facet) -> facetsNbt.put(key.id().toString(), facet.toNbt()));
        root.put(FacetRegistry.TAG_FACETS_ID, facetsNbt);
    }
}
