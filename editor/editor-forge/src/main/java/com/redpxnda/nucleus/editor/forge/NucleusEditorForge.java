package com.redpxnda.nucleus.editor.forge;

import com.redpxnda.nucleus.editor.NucleusEditor;
import imgui.ImGui;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(NucleusEditor.MOD_ID)
public class NucleusEditorForge {
    public NucleusEditorForge() {
        NucleusEditor.init();
        if (FMLEnvironment.dist.isClient()) {
            Minecraft.getInstance().submit(ClientLoaderForge::initClient);
            NucleusEditor.LOGGER.info("ImGui Version: " + ImGui.getVersion());
            for (var path : System.getProperty("java.library.path").split(";")) {
                if(path.contains("gui")){
                    System.out.println("Library Path: " + path);
                }
            }


        } else {
            NucleusEditor.LOGGER.error("Imgui is not useful on dedicated servers!");
        }
    }
}
