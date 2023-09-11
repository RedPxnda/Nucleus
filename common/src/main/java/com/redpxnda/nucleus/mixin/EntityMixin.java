package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.capability.entity.EntityCapability;
import com.redpxnda.nucleus.capability.entity.EntityDataSaver;
import com.redpxnda.nucleus.capability.entity.EntityDataRegistry;
import com.redpxnda.nucleus.resolving.wrappers.EntityWrapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Entity.class)
public class EntityMixin implements EntityWrapping, EntityDataSaver {
    @Unique
    private final Map<String, EntityCapability<?>> nucleus$caps = new HashMap<>();
    @Override
    public Map<String, EntityCapability<?>> getCapabilities() {
        return nucleus$caps;
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void addCustomSaveData(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        nucleus$caps.forEach((id, cap) -> tag.put(id, cap.toNbt()));
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void nucleus$loadCapabilities(CompoundTag tag, CallbackInfo ci) {
        if ((Object) this instanceof Entity entity) {
            EntityDataRegistry.CAPABILITIES.forEach((cap, holder) -> {
                if (holder.predicate().test(entity)) {
                    EntityCapability toLoad = holder.construct(entity);

                    String id = holder.id().toString();
                    if (tag.contains(id)) toLoad.loadNbt(tag.get(id)); // load nbt if present

                    nucleus$caps.put(id, toLoad);
                }
            });
        }
    }
}
