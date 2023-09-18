package com.redpxnda.nucleus.mixin.client;

import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class GuiMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nucleus$renderHudEvent(DrawContext guiGraphics, float f, CallbackInfo ci) {
        EventResult result = RenderEvents.HUD_RENDER_PRE.invoker().render(client, guiGraphics, f);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }
}
