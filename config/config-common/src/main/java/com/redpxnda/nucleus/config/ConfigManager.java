package com.redpxnda.nucleus.config;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.ops.JsoncOps;
import com.redpxnda.nucleus.config.network.clientbound.ConfigSyncPacket;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;

import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class ConfigManager {
    private static final Logger LOGGER = Nucleus.getLogger();
    public static final AtomicBoolean skipNextWatch = new AtomicBoolean(false);
    private static final Map<String, ConfigObject<?>> configs = new HashMap<>();

    /**
     * Register a new config
     */
    public static <T> ConfigObject<T> register(ConfigBuilder<T> builder) {
        ConfigObject<T> obj = builder.build();
        return register(obj);
    }

    public static <T> ConfigObject<T> register(ConfigObject<T> config) {
        ConfigObject<?> old = configs.get(config.name);
        if (old != null) throw new IllegalStateException("Two configs cannot be registered under the same name!\nOld: " + old + "\nNew: " + config);

        configs.put(config.name, config);
        return config;
    }

    /**
     * Use this carefully, it casts unsafely.
     */
    public static <T> T getConfig(String name) {
        return (T) configs.get(name).getInstance();
    }

    /**
     * Use this carefully, it casts unsafely.
     */
    public static <T> ConfigObject<T> getConfigObject(String name) {
        return (ConfigObject<T>) configs.get(name);
    }

    /**
     * do not call, this is for internal purposes
     */
    public static void init() {
        register(ConfigBuilder.automatic(NucleusConfig.class)
                .name("nucleus")
                .creator(NucleusConfig::new)
                .type(ConfigType.COMMON)
                .updateListener(config -> NucleusConfig.INSTANCE = config)
        );

        LifecycleEvent.SETUP.register(() -> {
            setupConfigs(ConfigType.COMMON);

            if (NucleusConfig.INSTANCE != null && NucleusConfig.INSTANCE.watchChanges)
                setupFileWatching();
        });
        LifecycleEvent.SERVER_STARTING.register(s -> setupConfigs(t -> t == ConfigType.SERVER_ONLY || t == ConfigType.SERVER_CLIENT_SYNCED));
        PlayerEvent.PLAYER_JOIN.register(sp -> configs.forEach((name, config) -> syncConfigWithPlayer(config, sp)));
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientLifecycleEvent.CLIENT_SETUP.register(s -> setupConfigs(ConfigType.CLIENT_ONLY)));
    }

    private static void setupFileWatching() {
        Thread thread = new Thread(() -> {
            try {
                Path configs = Platform.getConfigFolder();
                WatchService service = FileSystems.getDefault().newWatchService();
                configs.register(
                        service,
                        StandardWatchEventKinds.ENTRY_MODIFY
                );

                while (true) {
                    WatchKey key = service.take();

                    Thread.sleep(50); // Sleep to prevent picking up multiple of the same event
                    List<WatchEvent<?>> events = key.pollEvents();
                    if (!skipNextWatch.get()) {
                        for (WatchEvent<?> event : events) {
                            WatchEvent.Kind<?> kind = event.kind();
                            if (kind != StandardWatchEventKinds.ENTRY_MODIFY) return;
                            Path path = (Path) event.context();

                            if (path.toString().endsWith(".jsonc")) {
                                if (Platform.getEnv() == EnvType.CLIENT && MinecraftClient.getInstance() != null)
                                    MinecraftClient.getInstance().execute(() -> {
                                        String fileName = path.getFileName().toString();
                                        String configName = fileName.substring(0, fileName.length() - 6);
                                        ConfigObject<?> config = ConfigManager.getConfigObject(configName);
                                        if (config != null && config.watch && config.type.clientCanControl()) {
                                            LOGGER.info("File modification for client-sided config '{}' detected. Updating!", config.name);
                                            config.load();
                                            config.save();
                                        }
                                    });

                                if (Nucleus.SERVER != null)
                                    Nucleus.SERVER.execute(() -> {
                                        String fileName = path.getFileName().toString();
                                        String configName = fileName.substring(0, fileName.length() - 6);
                                        ConfigObject<?> config = ConfigManager.getConfigObject(configName);
                                        if (config != null && config.watch && config.type.serverCanControl()) {
                                            LOGGER.info("File modification for server-sided config '{}' detected. Updating!", config.name);
                                            config.load();
                                            config.save();

                                            if (config.type == ConfigType.SERVER_CLIENT_SYNCED)
                                                syncConfigWithAllPlayers(config, Nucleus.SERVER);
                                        }
                                    });
                            }
                        }
                    } else {
                        skipNextWatch.set(false);
                    }

                    boolean valid = key.reset(); // Reset the key
                    if (!valid) {
                        break; // The watch key is no longer valid, break the loop
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("Failed to setup config file watching!", e);
            }
        });
        thread.start();

        LOGGER.info("Successfully created file watcher (and thread) for config folder. ({})", Platform.getConfigFolder());
    }

    public static void syncConfigWithAllPlayers(ConfigObject<?> config, MinecraftServer server) {
        if (config.type != ConfigType.SERVER_CLIENT_SYNCED) return;
        new ConfigSyncPacket(config.name, config.serialize(JsoncOps.INSTANCE).toString()).send(server);
    }

    public static void syncConfigWithPlayer(ConfigObject<?> config, ServerPlayerEntity sp) {
        if (config.type != ConfigType.SERVER_CLIENT_SYNCED) return;
        new ConfigSyncPacket(config.name, config.serialize(JsoncOps.INSTANCE).toString()).send(sp);
    }

    private static void setupConfigs(ConfigType type) {
        setupConfigs(t -> t == type);
    }

    private static void setupConfigs(Predicate<ConfigType> condition) {
        configs.forEach((name, config) -> {
            if (!condition.test(config.type)) return;
            config.load();
            config.save();
        });
    }
}
