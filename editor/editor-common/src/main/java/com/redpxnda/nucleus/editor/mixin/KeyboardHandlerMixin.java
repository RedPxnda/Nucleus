package com.redpxnda.nucleus.editor.mixin;

import com.redpxnda.nucleus.editor.core.ImGuiMinecraft;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	@Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
	private void consumePress(CallbackInfo ci) {
		if (ImGuiMinecraft.consumeKeyPress())
			ci.cancel();
	}

	@Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
	private void consumeChar(CallbackInfo ci) {
		if (ImGuiMinecraft.consumeKeyPress())
			ci.cancel();
	}
}
