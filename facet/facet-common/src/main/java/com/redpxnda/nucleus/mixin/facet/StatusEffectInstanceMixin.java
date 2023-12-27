package com.redpxnda.nucleus.mixin.facet;

import com.redpxnda.nucleus.facet.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements FacetHolder {
    @Unique
    private final FacetInventory nucleus$facets = new FacetInventory();

    @Override
    public FacetInventory getFacets() {
        return nucleus$facets;
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/effect/StatusEffect;IIZZZLnet/minecraft/entity/effect/StatusEffectInstance;Ljava/util/Optional;)V", at = @At("RETURN"))
    private void nucleus$attachFacetsOnConstruction(StatusEffect type, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, StatusEffectInstance hiddenEffect, Optional factorCalculationData, CallbackInfo ci) {
        StatusEffectFacet.setupFacets((StatusEffectInstance) (Object) this);
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void nucleus$copyFacets(StatusEffectInstance that, CallbackInfo ci) {
        clearFacets();
        StatusEffectFacet.setupFacets((StatusEffectInstance) (Object) this);
        FacetInventory inv = FacetHolder.of(that).getFacets();
        nucleus$facets.forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet sef) {
                Facet<?> old = inv.get(key);
                if (old != null) sef.onCopied((StatusEffectFacet) old);
            }
        });
    }

    @Inject(method = "upgrade", at = @At("TAIL"))
    private void nucleus$upgradeFacets(StatusEffectInstance that, CallbackInfoReturnable<Boolean> cir) {
        FacetInventory inv = FacetHolder.of(that).getFacets();
        nucleus$facets.forEach((key, facet) -> {
            if (facet instanceof StatusEffectFacet sef)
                sef.attemptUpgradeWith((StatusEffectFacet) inv.get(key), that);
        });
    }

    @Inject(method = "writeTypelessNbt", at = @At("TAIL"))
    private void nucleus$writeFacetsToNbt(NbtCompound nbt, CallbackInfo ci) {
        StatusEffectFacet.writeFacetsToNbt(nbt, this);
    }

    @Inject(method = "fromNbt(Lnet/minecraft/entity/effect/StatusEffect;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/effect/StatusEffectInstance;", at = @At("TAIL"))
    private static void nucleus$readFacetsFromNbt(StatusEffect type, NbtCompound nbt, CallbackInfoReturnable<StatusEffectInstance> cir) {
        StatusEffectInstance instance = cir.getReturnValue();
        if (nbt.contains(FacetRegistry.TAG_FACETS_ID)) {
            NbtCompound facets = nbt.getCompound(FacetRegistry.TAG_FACETS_ID);
            FacetHolder.of(instance).getFacets().forEach((key, facet) -> {
                NbtElement element = facets.get(key.id().toString());
                FacetRegistry.loadNbtToFacet(element, key, facet);
            });
        }
    }

    @Inject(method = "applyUpdateEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffect;applyUpdateEffect(Lnet/minecraft/entity/LivingEntity;I)V"))
    private void nucleus$facetApplyEffectUpdate(LivingEntity entity, CallbackInfo ci) {
        nucleus$facets.forEach((facetKey, facet) -> {
            if (facet instanceof StatusEffectFacet<?,?> sef)
                sef.applyEffectUpdate(entity, (StatusEffectInstance) (Object) this);
        });
    }
}
