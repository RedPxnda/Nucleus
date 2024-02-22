package com.redpxnda.nucleus.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An event to add a config screen to your mod. See ScreenRegisterer.
 */
public interface ConfigScreensEvent {
    @Environment(EnvType.CLIENT)
    void add(ScreenRegisterer registerer);

    @Environment(EnvType.CLIENT)
    class ScreenRegisterer {
        protected final Map<String, Function<Screen, Screen>> screenFactories = new HashMap<>();

        /**
         * Add a custom screen to the specified mod.
         * @param modId   the id of the mod
         * @param factory the factory to create the screen - takes in the parent screen, returns your custom screen
         */
        public void add(String modId, Function<Screen, Screen> factory) {
            screenFactories.computeIfPresent(modId, (k, v) -> {
                throw new IllegalArgumentException("Cannot provide screen factory for a mod that already has one defined!");
            });
            screenFactories.put(modId, factory);
        }

        /**
         * Add an automatic screen to the specified mod. THIS ONLY WORKS IF YOUR CONFIG WAS MADE THROUGH {@link ConfigBuilder#automatic(Class)}!
         * @param modId      the id of the mod
         * @param configName the name of the config object to use
         */
        public void add(String modId, String configName) {
            add(modId, () -> ConfigManager.getConfigObject(configName));
        }

        /**
         * Add an automatic screen to the specified mod. THIS ONLY WORKS IF YOUR CONFIG WAS MADE THROUGH {@link ConfigBuilder#automatic(Class)}!
         * @param modId          the id of the mod
         * @param configSupplier a supplier providing the config object
         */
        public void add(String modId, Supplier<ConfigObject<?>> configSupplier) {
            add(modId, s -> { // supplier so that everything executes at the correct time.
                ConfigObject<?> config = configSupplier.get();
                assert config instanceof ConfigObject.Automatic<?> : "Config object must be 'automatic' to provide an automatic screen!";
                ConfigObject.Automatic<?> auto = ((ConfigObject.Automatic<?>) config);
                return auto.getScreen(s);
            });
        }

        public Map<String, Function<Screen, Screen>> getAllScreenFactories() {
            return screenFactories;
        }
    }
}
