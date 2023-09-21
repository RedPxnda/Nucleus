package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.capability.entity.EntityCapability;
import com.redpxnda.nucleus.capability.entity.EntityDataRegistry;
import com.redpxnda.nucleus.capability.entity.EntityDataSaver;
import com.redpxnda.nucleus.resolving.wrappers.EntityWrapping;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
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

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    private void addCustomSaveData(NbtCompound root, CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound tag = new NbtCompound();
        nucleus$caps.forEach((id, cap) -> tag.put(id, cap.toNbt()));
        root.put("nucleus:capabilities", tag);
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V"))
    private void nucleus$loadCapabilities(NbtCompound root, CallbackInfo ci) {
        if ((Object) this instanceof Entity entity && root.contains("nucleus:capabilities")) {
            NbtCompound tag = root.getCompound("nucleus:capabilities");
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
