package com.redpxnda.nucleus.editor.diagnostic;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ImGuiDebugPanels {
	private static final int[] SCREEN_SIZE = new int[2];
	private static final int[] GUI_SCALED_SIZE = new int[2];
	private static final float[] FBO_SIZE = new float[2];
	private static final int[] FBO_VIEW_SIZE = new int[2];
	public static ImBoolean SHOW = new ImBoolean(true);

	public static void onRender(GuiGraphics guiGraphics, DeltaTracker partialTick) {
		if(RenderSystem.isOnRenderThread()){
			screenSizingPanel();
		}
	}

	private static void screenSizingPanel() {
		ImGui.setNextWindowSize(350, 150, ImGuiCond.FirstUseEver);
		if (ImGui.begin("FBOs & Screen Readout", SHOW, ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoFocusOnAppearing)) {
			Window window = Minecraft.getInstance().getWindow();

			SCREEN_SIZE[0] = window.getScreenWidth();
			SCREEN_SIZE[1] = window.getScreenHeight();
			GUI_SCALED_SIZE[0] = window.getGuiScaledWidth();
			GUI_SCALED_SIZE[1] = window.getGuiScaledHeight();

			ImGui.inputInt2("Screen size", SCREEN_SIZE, ImGuiInputTextFlags.ReadOnly);
			ImGui.inputInt2("Gui Scaled", GUI_SCALED_SIZE, ImGuiInputTextFlags.ReadOnly);


			RenderTarget target = Minecraft.getInstance().getMainRenderTarget();

			FBO_SIZE[0] = target.width;
			FBO_SIZE[1] = target.height;
			FBO_VIEW_SIZE[0] = target.viewWidth;
			FBO_VIEW_SIZE[1] = target.viewHeight;

			ImGui.inputFloat2("FBO size", FBO_SIZE, "%.3f", ImGuiInputTextFlags.ReadOnly);
			ImGui.inputInt2("FBO view size", FBO_VIEW_SIZE, ImGuiInputTextFlags.ReadOnly);
		}
		ImGui.end();
	}
}
