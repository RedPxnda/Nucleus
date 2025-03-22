package com.redpxnda.nucleus.editor.fabric.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.redpxnda.nucleus.editor.core.ImGuiMinecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {


    @Inject(method = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V", at = @At("TAIL"), cancellable = true)
    private void miapi$teleportBlockEffect(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        RenderSystem.enableDepthTest();
        ImGuiMinecraft.renderOverlay(guiGraphics, deltaTracker);
        RenderSystem.disableDepthTest();
    }
}
