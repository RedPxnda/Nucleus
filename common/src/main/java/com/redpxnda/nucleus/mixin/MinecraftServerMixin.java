package com.redpxnda.nucleus.mixin;

import com.redpxnda.nucleus.event.ServerEvents;
import com.redpxnda.nucleus.resolving.wrappers.MinecraftServerWrapping;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerWrapping {
    @Shadow
    private MinecraftServer.ReloadableResources resources;

    @Inject(method = "reloadResources", at = @At("TAIL"))
    private void endResourceReload(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().handleAsync((value, throwable) -> {
            ServerEvents.END_DATA_PACK_RELOAD.invoker().endDataPackReload((MinecraftServer) (Object) this, this.resources.resourceManager(), throwable == null);
            return value;
        }, (MinecraftServer) (Object) this);
    }
}
