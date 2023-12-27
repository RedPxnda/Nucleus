package com.redpxnda.nucleus.mixin.facet;

import com.redpxnda.nucleus.facet.FacetAttachmentEvent;
import com.redpxnda.nucleus.facet.FacetHolder;
import com.redpxnda.nucleus.facet.FacetInventory;
import com.redpxnda.nucleus.facet.FacetRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.World;
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
    private void nucleus$setupFacets(EntityType type, World world, CallbackInfo ci) {
        FacetAttachmentEvent.FacetAttacher attacher = new FacetAttachmentEvent.FacetAttacher();
        FacetRegistry.ENTITY_FACET_ATTACHMENT.invoker().attach((Entity) (Object) this, attacher);
        setFacetsFromAttacher(attacher);
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    private void nucleus$saveFacets(NbtCompound root, CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound tag = new NbtCompound();
        nucleus$facets.forEach((key, facet) -> tag.put(key.id().toString(), facet.toNbt()));
        root.put(FacetRegistry.TAG_FACETS_ID, tag);
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    private void nucleus$loadFacets(NbtCompound root, CallbackInfo ci) {
        if (root.contains(FacetRegistry.TAG_FACETS_ID)) {
            NbtCompound tag = root.getCompound(FacetRegistry.TAG_FACETS_ID);
            nucleus$facets.forEach((key, facet) -> {
                NbtElement element = tag.get(key.id().toString());
                FacetRegistry.loadNbtToFacet(element, key, facet);
            });
        }
    }
}
