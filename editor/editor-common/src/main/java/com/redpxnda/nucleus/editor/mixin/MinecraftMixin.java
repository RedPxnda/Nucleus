package com.redpxnda.nucleus.editor.mixin;

import com.redpxnda.nucleus.editor.core.ImGuiMinecraft;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V", shift = At.Shift.AFTER))
	private void setupImGuiFrame(boolean pRenderLevel, CallbackInfo ci) {
		ImGuiMinecraft.setupFrame();
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen(II)V", shift = At.Shift.AFTER))
	private void finishImGuiFrame(boolean pRenderLevel, CallbackInfo ci) {
		ImGuiMinecraft.finishFrame(false);
	}

	@Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/VirtualScreen;close()V", shift = At.Shift.BEFORE))
	private void closeImGui(CallbackInfo ci) {
		ImGuiMinecraft.dispose();
	}
}
