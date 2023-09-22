package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.capability.item.ItemStackCapability;
import com.redpxnda.nucleus.capability.item.ItemStackDataRegistry;
import com.redpxnda.nucleus.capability.item.ItemStackDataSaver;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackDataSaver {
    @Unique
    private final Map<String, ItemStackCapability<?>> nucleus$caps = new HashMap<>();
    @Override
    public Map<String, ItemStackCapability<?>> getCapabilities() {
        return nucleus$caps;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    private void nucleus$saveCapabilities(NbtCompound root, CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound tag = new NbtCompound();
        nucleus$caps.forEach((id, cap) -> tag.put(id, cap.toNbt()));
        root.put("nucleus:capabilities", tag);
    }

    @Inject(method = "fromNbt", at = @At("RETURN"))
    private static void nucleus$readCapabilities(NbtCompound root, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        nucleus$loadCaps(stack, root);
    }

    private static void nucleus$loadCaps(ItemStack stack, NbtCompound root) {
        if (stack.isEmpty()) return;
        if (root.contains("nucleus:capabilities")) {
            NbtCompound tag = root.getCompound("nucleus:capabilities");
            ItemStackDataRegistry.CAPABILITIES.forEach((cap, holder) -> {
                if (holder.predicate().test(stack)) {
                    ItemStackCapability toLoad = holder.construct(stack);

                    String id = holder.id().toString();
                    if (tag.contains(id)) toLoad.loadNbt(tag.get(id)); // load nbt if present

                    nucleus$getAsDataSaver(stack).getCapabilities().put(id, toLoad);
                }
            });
        }
    }

    @Inject(method = "copy", at = @At("TAIL"))
    private void nucleus$copyCapabilities(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack newStack = cir.getReturnValue();
        if (newStack.hasNbt()) {
            nucleus$caps.forEach((id, cap) -> {
                ItemStackCapability newCap = cap.createCopy();
                if (cap.getClass() != newCap.getClass()) throw new IllegalStateException("ItemStackCapability copy method unexpectedly did not return a capability of the same class!");
                nucleus$getAsDataSaver(newStack).getCapabilities().put(id, newCap);
            });
        }
    }

    private static ItemStackDataSaver nucleus$getAsDataSaver(ItemStack item) {
        return ((ItemStackDataSaver) (Object) item);
    }
}
