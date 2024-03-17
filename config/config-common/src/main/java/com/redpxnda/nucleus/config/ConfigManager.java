package com.redpxnda.nucleus.config;

import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.ops.JsoncOps;
import com.redpxnda.nucleus.config.network.clientbound.ConfigSyncPacket;
import com.redpxnda.nucleus.event.PrioritizedEvent;
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
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class ConfigManager {
    private static final Logger LOGGER = Nucleus.getLogger();
    public static final AtomicBoolean skipNextWatch = new AtomicBoolean(false);
    private static final Map<Identifier, ConfigObject<?>> configs = new HashMap<>();
    private static final Map<String, ConfigObject<?>> configsByFileLocation = new HashMap<>();
    public static final PrioritizedEvent<ConfigScreensEvent> CONFIG_SCREENS_REGISTRY = PrioritizedEvent.createLoop();

    /**
     * Register a new config
     */
    public static <T> ConfigObject<T> register(ConfigBuilder<T> builder) {
        ConfigObject<T> obj = builder.build();
        return register(obj);
    }

    public static <T> ConfigObject<T> register(ConfigObject<T> config) {
        ConfigObject<?> old = configs.get(config.id);
        if (old != null) throw new IllegalStateException("Two configs cannot be registered under the same id!\nOld: " + old + "\nNew: " + config);

        configsByFileLocation.put(config.fileLocation, config);
        configs.put(config.id, config);
        return config;
    }

    /**
     * Use this carefully, it casts unsafely.
     */
    public static <T> T getConfig(Identifier id) {
        return (T) configs.get(id).getInstance();
    }

    /**
     * Use this carefully, it casts unsafely.
     */
    public static <T> ConfigObject<T> getConfigObject(Identifier id) {
        return (ConfigObject<T>) configs.get(id);
    }

    /**
     * Use this carefully, it casts unsafely.
     */
    public static <T> ConfigObject<T> getConfigObjectByFileLocation(String fileLocation) {
        return (ConfigObject<T>) configsByFileLocation.get(fileLocation);
    }

    /**
     * Use this carefully, it casts unsafely.
     */
    public static <T> ConfigObject<T> getConfigObjectByPath(Path path) {
        String relative = Platform.getConfigFolder().relativize(path).toString();
        String location = relative.substring(0, relative.length() - 6).replace('\\', '/'); // 6 = ".jsonc".length()
        ConfigObject<?> config = ConfigManager.getConfigObjectByFileLocation(location);
        return (ConfigObject<T>) config;
    }



    /**
     * do not call, this is for internal purposes
     */
    public static void init() {
        register(ConfigBuilder.automatic(NucleusConfig.class)
                .id("nucleus:configs")
                .fileLocation("nucleus/configs")
                .creator(NucleusConfig::new)
                .type(ConfigType.COMMON)
                .updateListener(config -> NucleusConfig.INSTANCE = config)
        );

        LifecycleEvent.SETUP.register(() -> setupConfigs(ConfigType.COMMON));
        LifecycleEvent.SERVER_STARTING.register(s -> setupConfigs(t -> t == ConfigType.SERVER_ONLY || t == ConfigType.SERVER_CLIENT_SYNCED));
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientLifecycleEvent.CLIENT_SETUP.register(s -> setupConfigs(ConfigType.CLIENT_ONLY)));

        PlayerEvent.PLAYER_JOIN.register(sp -> configs.forEach((name, config) -> syncConfigWithPlayer(config, sp)));

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientLifecycleEvent.CLIENT_STARTED.register(s -> {
            if (NucleusConfig.INSTANCE != null && NucleusConfig.INSTANCE.watchChanges)
                setupFileWatching();
        }));
        EnvExecutor.runInEnv(Env.SERVER, () -> () -> LifecycleEvent.SERVER_STARTED.register(s -> {
            if (NucleusConfig.INSTANCE != null && NucleusConfig.INSTANCE.watchChanges)
                setupFileWatching();
        }));
    }

    private static void registerWatchingForSubpaths(Path start, WatchService watchService, WatchEvent.Kind<?>... events) throws IOException {
        // Register the base directory
        start.register(watchService, events);

        // Register all subdirectories recursively
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, events);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void setupFileWatching() {
        Thread thread = new Thread(() -> {
            try {
                Path configs = Platform.getConfigFolder();
                WatchService service = FileSystems.getDefault().newWatchService();
                registerWatchingForSubpaths(configs, service, StandardWatchEventKinds.ENTRY_MODIFY);

                boolean shouldSleep = false; // used to prevent sleeping on first update
                while (true) {
                    WatchKey key = service.take();

                    if (shouldSleep)
                        Thread.sleep(50); // Sleep to prevent picking up multiple of the same event
                    else shouldSleep = true;
                    List<WatchEvent<?>> events = key.pollEvents();
                    if (!skipNextWatch.get()) {
                        for (WatchEvent<?> event : events) {
                            WatchEvent.Kind<?> kind = event.kind();
                            if (kind != StandardWatchEventKinds.ENTRY_MODIFY) return;
                            Path path = ((Path) key.watchable()).resolve((Path) event.context());

                            if (path.toString().endsWith(".jsonc")) {
                                if (Platform.getEnv() == EnvType.CLIENT && MinecraftClient.getInstance() != null)
                                    MinecraftClient.getInstance().execute(() -> {
                                        ConfigObject<?> config = ConfigManager.getConfigObjectByPath(path);
                                        if (config != null && config.watch && config.type.clientCanControl()) {
                                            LOGGER.info("File modification for client-sided config '{}' detected. Updating!", config.id);
                                            config.load();
                                            config.save();
                                        }
                                    });

                                if (Nucleus.SERVER != null)
                                    Nucleus.SERVER.execute(() -> {
                                        ConfigObject<?> config = ConfigManager.getConfigObjectByPath(path);
                                        if (config != null && config.watch && config.type.serverCanControl()) {
                                            LOGGER.info("File modification for server-sided config '{}' detected. Updating!", config.id);
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
        new ConfigSyncPacket(config.id, config.serialize(JsoncOps.INSTANCE).toString()).send(server);
    }

    public static void syncConfigWithPlayer(ConfigObject<?> config, ServerPlayerEntity sp) {
        if (config.type != ConfigType.SERVER_CLIENT_SYNCED) return;
        new ConfigSyncPacket(config.id, config.serialize(JsoncOps.INSTANCE).toString()).send(sp);
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
