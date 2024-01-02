package com.redpxnda.nucleus.test.fabric;

import com.redpxnda.nucleus.config.ConfigManager;
import com.redpxnda.nucleus.config.ConfigObject;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> ((ConfigObject.Automatic<?>) ConfigManager.getConfigObject("nucleus-test-common")).getScreen(screen);
    }
}
