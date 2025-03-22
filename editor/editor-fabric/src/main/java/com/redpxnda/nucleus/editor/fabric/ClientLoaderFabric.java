package com.redpxnda.nucleus.editor.fabric;


import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ClientLoaderFabric {

    static void initClient() {

        if (System.getProperty("os.arch").equals("arm") || System.getProperty("os.arch").startsWith("aarch64"))
            setupImGuiLibARM64();
    }

    private static void setupImGuiLibARM64() {
        System.setProperty("imgui.library.name", "libimgui-javaarm64.dylib");

        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        // Define the base path and the relative path to the native library. Expects game root dir to be /runs/client/.
        String relativePath = "../../build/resources/main/io/imgui/java/native-bin/";
        // Resolve the absolute path to the native library
        Path nativeLibPath = FabricLoader.getInstance().getGameDir().resolve(relativePath).normalize();

        System.setProperty("imgui.library.path", nativeLibPath.toAbsolutePath().toString());
    }
}
