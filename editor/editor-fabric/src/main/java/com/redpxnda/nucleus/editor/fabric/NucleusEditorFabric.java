package com.redpxnda.nucleus.editor.fabric;

import com.redpxnda.nucleus.editor.NucleusEditor;
import com.redpxnda.nucleus.editor.core.ClientLoader;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public class NucleusEditorFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        NucleusEditor.init();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            Minecraft.getInstance().submit(ClientLoaderFabric::initClient);
        }
    }
}
