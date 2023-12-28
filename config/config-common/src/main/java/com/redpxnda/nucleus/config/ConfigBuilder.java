package com.redpxnda.nucleus.config;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.auto.ConfigAutoCodec;
import dev.architectury.platform.Platform;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigBuilder<T> {
    protected Path path = Platform.getConfigFolder();
    protected String name;
    protected ConfigType type;
    protected Codec<T> codec;
    protected Supplier<T> creator;
    protected @Nullable Consumer<T> onUpdate;
    protected boolean watch = true;

    /**
     * Creates an automatic(scans the class's fields, see {@link AutoCodec} and {@link ConfigAutoCodec}) config builder.
     */
    public static <T> ConfigBuilder<T> automatic(Class<T> cls) {
        return new Automatic<>(cls);
    }

    /**
     * Create a custom config builder with the specified codec.
     */
    public static <T> ConfigBuilder<T> custom(Codec<T> codec) {
        return new ConfigBuilder<T>().codec(codec);
    }

    public ConfigBuilder() {}

    /**
     * Builds the config
     */
    public ConfigObject<T> build() {
        if (name == null || type == null || codec == null || creator == null)
            throw new UnsupportedOperationException("ConfigBuilder incomplete! You must define a name, type, codec, and creator.");
        return new ConfigObject<>(path, name, type, codec, creator, onUpdate, watch, null);
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

    /**
     * Defines whether file changes should automatically update this config (default true)
     */
    public ConfigBuilder<T> watch(boolean watched) {
        this.watch = watched;
        return this;
    }

    protected ConfigBuilder<T> codec(Codec<T> codec) {
        this.codec = codec;
        return this;
    }

    public static class Automatic<T> extends ConfigBuilder<T> {
        public Automatic(Class<T> cls) {
            this.codec = ConfigAutoCodec.of(cls, creator).codec();
        }

        @Override
        public ConfigObject.Automatic<T> build() {
            if (name == null || type == null || codec == null || creator == null)
                throw new UnsupportedOperationException("ConfigBuilder incomplete! You must define a name, type, codec, and creator.");
            return new ConfigObject.Automatic<>(path, name, type, codec, creator, onUpdate, watch, null);
        }
    }
}
