package com.redpxnda.nucleus.facet.mixin;

import com.redpxnda.nucleus.facet.event.FacetAttachmentEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import com.redpxnda.nucleus.facet.FacetHolder;
import com.redpxnda.nucleus.facet.FacetInventory;
import com.redpxnda.nucleus.facet.FacetRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements FacetHolder {
    @Unique
    private final FacetInventory nucleus$facets = new FacetInventory();
    
    @Override
    public FacetInventory getFacets() {
        return nucleus$facets;
    }
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void nucleus$setupFacets(EntityType type, Level world, CallbackInfo ci) {
        FacetAttachmentEvent.FacetAttacher attacher = new FacetAttachmentEvent.FacetAttacher();
        FacetRegistry.ENTITY_FACET_ATTACHMENT.invoker().attach((Entity) (Object) this, attacher);
        setFacetsFromAttacher(attacher);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void nucleus$saveFacets(CompoundTag root, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = new CompoundTag();
        nucleus$facets.forEach((key, facet) -> {
            if(facet.shouldSave()){
                tag.put(key.id().toString(), facet.toNbt());
            }
        });
        root.put(FacetRegistry.TAG_FACETS_ID, tag);
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void nucleus$loadFacets(CompoundTag root, CallbackInfo ci) {
        if (root.contains(FacetRegistry.TAG_FACETS_ID)) {
            CompoundTag tag = root.getCompound(FacetRegistry.TAG_FACETS_ID);
            nucleus$facets.forEach((key, facet) -> {
                Tag element = tag.get(key.id().toString());
                FacetRegistry.loadNbtToFacet(element, key, facet);
            });
        }
    }
}
