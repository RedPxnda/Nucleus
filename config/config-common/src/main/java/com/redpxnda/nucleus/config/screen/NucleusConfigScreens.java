package com.redpxnda.nucleus.config.screen;

import com.redpxnda.nucleus.config.ConfigObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class NucleusConfigScreens {

    /**
     * Opens a multi-config screen for a given mod ID and list of Automatic configs.
     * @param parent The parent screen
     * @param modId The mod ID (used in the title)
     * @param configs The list of Automatic configs
     */
    public static void openMultiConfigScreen(Screen parent, String modId, List<ConfigObject.Automatic<?>> configs) {
        if (configs.isEmpty()) return;

        // Wrap configs with names for tabs
        List<MultiConfigScreen.TypedConfig<?>> tabs = new ArrayList<>();
        for (ConfigObject.Automatic<?> cfg : configs) {
            String name = cfg.id.getPath(); // use the resource location path as tab name
            tabs.add(new MultiConfigScreen.TypedConfig<>(name, cfg));
        }

        Minecraft.getInstance().setScreen(new MultiConfigScreen(parent, tabs));
    }

    /**
     * Helper to register a supplier that creates a multi-config screen.
     * Example usage:
     * <pre>
     * ConfigManager.CONFIG_SCREENS_REGISTRY.register(registerer -> {
     *     registerer.add(MOD_ID, parent -> NucleusConfigScreens.createMultiScreenSupplier(parent, MOD_ID,
     *         List.of(clientConfig, serverConfig)));
     * });
     * </pre>
     * @param parent The parent screen
     * @param modId The mod ID
     * @param configs List of Automatic configs
     * @return A supplier that returns a MultiConfigScreen
     */
    public static Screen createMultiScreenSupplier(Screen parent, String modId, List<ConfigObject.Automatic<?>> configs) {
        return new MultiConfigScreen(parent, configs.stream()
                .map(cfg -> new MultiConfigScreen.TypedConfig<>(cfg.id.getPath(), cfg))
                .toList());
    }
}
