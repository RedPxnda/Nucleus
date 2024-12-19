package com.redpxnda.nucleus.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.redpxnda.nucleus.event.ClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.server.packs.resources.ResourceManager;

@Mixin(ClientLanguage.class)
public class TranslationStorageMixin {
    @Inject(
            method = "loadFrom",
            at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap;copyOf(Ljava/util/Map;)Lcom/google/common/collect/ImmutableMap;", remap = false))
    private static void nucleus$adjustTranslationsEvent(ResourceManager resourceManager, List<String> definitions, boolean rightToLeft, CallbackInfoReturnable<ClientLanguage> cir, @Local(ordinal = 0) Map<String, String> map) {
        ClientEvents.TRANSLATIONS_RELOADED.invoker().adjust(map);
    }
}
