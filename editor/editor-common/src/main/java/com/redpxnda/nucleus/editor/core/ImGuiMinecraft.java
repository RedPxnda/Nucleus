package com.redpxnda.nucleus.editor.core;

import com.redpxnda.nucleus.editor.NucleusEditor;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiContext;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ImGuiMinecraft {
    public static final ImGuiImplGl3 IMGUI_GL3 = new ImGuiImplGl3();
    public static final ImGuiContext IMGUI_CONTEXT = ImGui.createContext();
    public static final ImGuiImplGlfw IMGUI_GLFW = new ImGuiImplGlfw();

    private static boolean consumeClick;
    private static boolean consumeKeyPress;

    private static boolean frameActive = false;

    static {
        ImGuiMinecraft.setupImGui(genImguiGlVersionString(), !Minecraft.ON_OSX);
    }

    public static void setupImGui(final String glVersionString, boolean multiViewport) {
        ImGuiIO io = ImGui.getIO();

        if (multiViewport) // FIXME Crash
            io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        io.setBackendPlatformName("ImGui Minecraft");
        io.setConfigWindowsMoveFromTitleBarOnly(true);
        io.setIniFilename(null); // Do not save .ini file

        NucleusEditor.LOGGER.debug("Initializing ImGui with GLSL #version {}", glVersionString);
        IMGUI_GLFW.init(Minecraft.getInstance().getWindow().getWindow(), true);
        IMGUI_GL3.init("#version " + glVersionString);
    }

    public static void setupFrame() {
        if (frameActive) {
            NucleusEditor.LOGGER.error("setupFrame was called again before finishFrame!");
            finishFrame(true);
        }

        frameActive = true;

        //IMGUI_GLFW.init(Minecraft.getInstance().getWindow().getWindow(), true);
        IMGUI_GLFW.newFrame();
        IMGUI_GL3.newFrame();

        //ImGuiIO io = ImGui.getIO();
        //io.getFontDefault();
        //io.Fonts.addFontDefault(); // Ensure default font is added
        //io.Fonts.build(); // This is required!

        ImGui.newFrame();
    }

    public static void finishFrame(boolean preSetup) {
        if (preSetup) {
            ImGui.endFrame();
        } else if (frameActive) {
            ImGui.render();
            IMGUI_GL3.renderDrawData(ImGui.getDrawData());

            ImGuiIO io = ImGui.getIO();
            consumeClick = io.getWantCaptureMouse() && !Minecraft.getInstance().mouseHandler.isMouseGrabbed();
            consumeKeyPress = io.getWantCaptureKeyboard();

            if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);
                var backupWindowPtr = GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(backupWindowPtr);
            }
        }

        frameActive = false;
    }

    public static void dispose() {
        //IMGUI_GL3.dispose();
        //IMGUI_GLFW.dispose();
    }

    public static boolean consumeClick() {
        return consumeClick;
    }

    public static boolean consumeKeyPress() {
        return consumeKeyPress;
    }

    // https://github.com/mattdesl/lwjgl-basics/wiki/GLSL-Versions#glsl-versions
    public static String genImguiGlVersionString() {
        String[] glVersion = new String[]{};//ImmediateWindowHandler.getGLVersion().split("\\.");
        if (glVersion.length <= 2 && glVersion.length > 2) {
            String glslVersionStr = glVersion[0] + glVersion[1] + "0";
            return switch (glslVersionStr) {
                case "200" -> "110";
                case "210" -> "120";
                case "300" -> "130";
                case "310" -> "140";
                case "320" -> "150";
                // OpenGL 3.3, 4.0, 4.1,... instead use 330, 400, 410,...
                default -> glslVersionStr;
            };
        }

        return "150";
    }

}
