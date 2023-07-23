package com.redpxnda.nucleus.mixin.client;

import com.redpxnda.nucleus.event.RenderEvents;
import dev.architectury.event.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nucleus$renderHudEvent(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        EventResult result = RenderEvents.HUD_RENDER_PRE.invoker().render(minecraft, guiGraphics, f);
        if (result.interruptsFurtherEvaluation())
            ci.cancel();
    }
}
