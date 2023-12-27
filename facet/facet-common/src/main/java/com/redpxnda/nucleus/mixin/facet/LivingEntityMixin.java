package com.redpxnda.nucleus.mixin.facet;

import com.redpxnda.nucleus.facet.FacetHolder;
import com.redpxnda.nucleus.facet.StatusEffectFacet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "onStatusEffectApplied",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onApplied(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/attribute/AttributeContainer;I)V"))
    private void nucleus$callStatusEffectFacetApplied(StatusEffectInstance effect, Entity source, CallbackInfo ci) {
        FacetHolder.of(effect).getFacets().forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.onApplied((LivingEntity) (Object) this, effect);
        });
    }

    @Inject(
            method = "onStatusEffectUpgraded",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onApplied(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/attribute/AttributeContainer;I)V"))
    private void nucleus$callStatusEffectFacetAppliedOnChange(StatusEffectInstance effect, boolean reapplyEffect, Entity source, CallbackInfo ci) {
        FacetHolder.of(effect).getFacets().forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.onApplied((LivingEntity) (Object) this, effect);
        });
    }

    @Inject(
            method = "onStatusEffectUpgraded",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onRemoved(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/attribute/AttributeContainer;I)V"))
    private void nucleus$callStatusEffectFacetRemovedOnChange(StatusEffectInstance effect, boolean reapplyEffect, Entity source, CallbackInfo ci) {
        FacetHolder.of(effect).getFacets().forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.onRemoved((LivingEntity) (Object) this, effect);
        });
    }

    @Inject(
            method = "onStatusEffectRemoved",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;onRemoved(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/attribute/AttributeContainer;I)V"))
    private void nucleus$callStatusEffectFacetRemoved(StatusEffectInstance effect, CallbackInfo ci) {
        FacetHolder.of(effect).getFacets().forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.onRemoved((LivingEntity) (Object) this, effect);
        });
    }
}
