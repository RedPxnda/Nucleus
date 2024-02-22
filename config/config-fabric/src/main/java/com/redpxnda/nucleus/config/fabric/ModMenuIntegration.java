package com.redpxnda.nucleus.config.fabric;

import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.config.ConfigScreensEvent;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.util.HashMap;
import java.util.Map;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        ConfigScreensEvent.ScreenRegisterer registerer = new ConfigScreensEvent.ScreenRegisterer();
        ConfigManager.CONFIG_SCREENS_REGISTRY.invoker().add(registerer);

        Map<String, ConfigScreenFactory<?>> factories = new HashMap<>();
        registerer.getAllScreenFactories().forEach((k, v) -> factories.put(k, v::apply));
        return factories;
    }
}
