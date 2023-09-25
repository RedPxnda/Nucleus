package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.MiscEvents;
import com.redpxnda.nucleus.facet.FacetHolder;
import com.redpxnda.nucleus.facet.StatusEffectFacet;
import com.redpxnda.nucleus.resolving.wrappers.LivingEntityWrapping;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements LivingEntityWrapping {
    @Inject(
            method = "jump",
            at = @At("HEAD"),
            cancellable = true)
    private void nucleus$entityJumpEvent(CallbackInfo ci) {
        EventResult result = MiscEvents.LIVING_JUMP.invoker().call((LivingEntity) (Object) this);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }

    @Inject(
            method = "getJumpVelocity",
            at = @At("HEAD"),
            cancellable = true)
    private void nucleus$entityJumpPowerEvent(CallbackInfoReturnable<Float> cir) {
        CompoundEventResult<Float> result = MiscEvents.LIVING_JUMP_POWER.invoker().call((LivingEntity) (Object) this);
        if (result.interruptsFurtherEvaluation())
            cir.setReturnValue(result.object());
    }

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
