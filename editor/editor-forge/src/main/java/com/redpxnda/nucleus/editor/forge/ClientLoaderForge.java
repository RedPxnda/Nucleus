package com.redpxnda.nucleus.editor.forge;

import com.redpxnda.nucleus.editor.NucleusEditor;
import com.redpxnda.nucleus.editor.core.ImGuiMinecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.nio.file.Path;

@EventBusSubscriber(modid = NucleusEditor.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientLoaderForge {
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerBelowAll(ResourceLocation.parse(NucleusEditor.MOD_ID + ":render_imgui"), ImGuiMinecraft::renderOverlay);
    }

    static void initClient() {
        if (System.getProperty("os.arch").equals("arm") || System.getProperty("os.arch").startsWith("aarch64"))
            setupImGuiLibARM64();
        setupImGuiLibARM64();
    }

    private static void setupImGuiLibARM64() {
        System.setProperty("imgui.library.name", "libimgui-javaarm64.dylib");

        if (FMLLoader.isProduction()) return;

        // Define the base path and the relative path to the native library. Expects game root dir to be /runs/client/.
        String relativePath = "../build/resources/main/io/imgui/java/native-bin";
        // Resolve the absolute path to the native library
        Path nativeLibPath = FMLPaths.GAMEDIR.get().resolve(relativePath).normalize();

        System.setProperty("imgui.library.path", nativeLibPath.toAbsolutePath().toString());
    }
}
