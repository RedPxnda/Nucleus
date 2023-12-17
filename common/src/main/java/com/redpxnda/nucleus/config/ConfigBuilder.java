package com.redpxnda.nucleus.config;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.ConfigAutoCodec;
import dev.architectury.platform.Platform;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigBuilder<T> {
    private Path path = Platform.getConfigFolder();
    private String name;
    private ConfigType type;
    private Codec<T> codec;
    private Supplier<T> creator;
    private @Nullable Consumer<T> onUpdate;

    /**
     * Create a new config builder. The {@code cls} is just for generics.
     */
    public static <T> ConfigBuilder<T> create(Class<T> cls) {
        return new ConfigBuilder<>();
    }

    public ConfigBuilder() {}

    /**
     * Builds the config
     */
    public ConfigObject<T> build() {
        if (name == null || type == null || codec == null || creator == null)
            throw new UnsupportedOperationException("ConfigBuilder incomplete! You must define a name, type, codec, and creator.");
        return new ConfigObject<>(path, name, type, codec, creator, onUpdate, null);
    }

    /**
     * Defines the subpath for this builder.
     * Normally, the path to this config will just be <b>config/[the_name].json</b>. Using this method will turn this into <b>config/[the_subpath]/[the_name].json</b>
     */
    public ConfigBuilder<T> path(String subpath) {
        this.path = Platform.getConfigFolder().resolve(subpath);
        return this;
    }

    public ConfigBuilder<T> path(Path subpath) {
        this.path = Platform.getConfigFolder().resolve(subpath);
        return this;
    }

    /**
     * Defines the name for this config. This will be your config's file name, excluding the extension.
     * This will normally be set to your mod name + your config type, but it doesn't have to be.
     */
    public ConfigBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Defines the type for this config. See {@link ConfigType}.
     */
    public ConfigBuilder<T> type(ConfigType type) {
        this.type = type;
        return this;
    }

    /**
     * Defines the codec to determine how your config will be serialized and deserialized.
     * Normally, you can use the {@link ConfigAutoCodec} via {@link ConfigBuilder#forClass(Class) forClass}
     */
    public ConfigBuilder<T> codec(Codec<T> codec) {
        this.codec = codec;
        return this;
    }

    /**
     * Convenience method to automatically scan your config class. DEFINE THE CREATOR BEFORE DEFINING THIS!
     */
    public ConfigBuilder<T> forClass(Class<T> cls) {
        this.codec = ConfigAutoCodec.of(cls, creator).codec();
        return this;
    }

    /**
     * Defines a supplier creating the default instance of this config, for when there is no file already.
     */
    public ConfigBuilder<T> creator(Supplier<T> creator) {
        this.creator = creator;
        return this;
    }

    /**
     * Defines a listener for when this config is updated.
     */
    public ConfigBuilder<T> updateListener(Consumer<T> onUpdate) {
        this.onUpdate = onUpdate;
        return this;
    }
}
