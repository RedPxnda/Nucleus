package com.redpxnda.nucleus.config;

import com.redpxnda.nucleus.codec.JsoncOps;
import com.redpxnda.nucleus.network.clientbound.ConfigSyncPacket;
import com.redpxnda.nucleus.util.Comment;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Map<String, ConfigObject<?>> configs = new HashMap<>();

    /**
     * Register a new config
     */
    public static <T> ConfigObject<T> register(ConfigBuilder<T> builder) {
        ConfigObject<T> obj = builder.build();
        configs.put(obj.name(), obj);
        return obj;
    }

    public static <T> ConfigObject<T> register(ConfigObject<T> config) {
        configs.put(config.name(), config);
        return config;
    }

    /**
     * Use this carefully, it casts unsafely.
     */
    public static <T> T getConfig(String name) {
        return (T) configs.get(name).instance();
    }

    /**
     * Use this carefully, it casts unsafely.
     */
    public static <T> ConfigObject<T> getConfigObject(String name) {
        return (ConfigObject<T>) configs.get(name);
    }

    public static class TestConfig {
        @Comment("This integer does stuff")
        public int someCoolInteger = 5;

        @Comment("So, this is a string")
        public String stringConfigSetting = "default value!";

        @Comment("""
                some random item
                with a multiline comment
                """)
        public Item someItem = Items.ICE;
    }

    /**
     * do not call, this is for internal purposes
     */
    public static void init() {
        register(ConfigBuilder.create(TestConfig.class).name("nucleus-test").creator(TestConfig::new).type(ConfigType.SERVER_CLIENT_SYNCED).forClass(TestConfig.class));

        LifecycleEvent.SETUP.register(() -> setupConfigs(ConfigType.COMMON));
        LifecycleEvent.SERVER_STARTING.register(s -> setupConfigs(ConfigType.SERVER_ONLY, ConfigType.SERVER_CLIENT_SYNCED));
        PlayerEvent.PLAYER_JOIN.register(sp -> configs.forEach((name, config) -> {
            if (config.type() != ConfigType.SERVER_CLIENT_SYNCED) return;
            new ConfigSyncPacket(name, config.serialize(JsoncOps.INSTANCE).toString()).send(sp);
        }));

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientLifecycleEvent.CLIENT_SETUP.register(s -> setupConfigs(ConfigType.CLIENT_ONLY)));
    }

    private static void setupConfigs(ConfigType type) {
        configs.forEach((name, config) -> {
            if (config.type() != type) return;
            config.load();
            config.save();
        });
    }

    private static void setupConfigs(ConfigType type, ConfigType type2) {
        configs.forEach((name, config) -> {
            if (config.type() != type && config.type() != type2) return;
            config.load();
            config.save();
        });
    }
}
