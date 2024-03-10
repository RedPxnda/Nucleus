package com.redpxnda.nucleus.config;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.codec.auto.ConfigAutoCodec;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigBuilder<T> {
    protected String fileLocation;
    protected Identifier id;
    protected ConfigType type;
    protected Codec<T> codec;
    protected Supplier<T> creator;
    protected @Nullable Consumer<T> onUpdate;
    protected @Nullable Function<T, ConfigPreset<T, ?>> presetGetter;
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
        if (id == null || type == null || codec == null || creator == null)
            throw new UnsupportedOperationException("ConfigBuilder incomplete! You must define a name, type, codec, and creator.");
        return new ConfigObject<>(fileLocation, id, type, codec, creator, onUpdate, presetGetter, watch, null);
    }

    /**
     * Defines the file location for this config.
     * This will be relative to the config folder, and includes the file name(but the file extension is added automatically. Leave that out).
     */
    public ConfigBuilder<T> fileLocation(String location) {
        this.fileLocation = location;
        return this;
    }

    /**
     * Defines the id for this config. Every config should have a unique id.
     * This will set the file location for you, so make sure to call file location after this.
     */
    public ConfigBuilder<T> id(Identifier id) {
        this.id = id;
        this.fileLocation = id.getNamespace() + "-" + id.getPath();
        return this;
    }

    public ConfigBuilder<T> id(String id) {
        return id(new Identifier(id));
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
     * Defines a way to obtain a {@link ConfigPreset preset} from this config
     */
    public ConfigBuilder<T> presetGetter(Function<T, ConfigPreset<T, ?>> presetGetter) {
        this.presetGetter = presetGetter;
        return this;
    }

    /**
     * Defines whether file changes should automatically update this config (default true)
     */
    public ConfigBuilder<T> watch(boolean watched) {
        this.watch = watched;
        return this;
    }

    public ConfigBuilder<T> automaticScreen() {
        assert this instanceof ConfigBuilder.Automatic<T> : "Config must be automatic to create an automatic screen!";
        if (Platform.getEnv() == EnvType.CLIENT)
            ConfigManager.CONFIG_SCREENS_REGISTRY.register(r -> r.add(id.getNamespace(), id));
        return this;
    }

    protected ConfigBuilder<T> codec(Codec<T> codec) {
        this.codec = codec;
        return this;
    }

    public static class Automatic<T> extends ConfigBuilder<T> {
        protected final Class<T> cls;
        protected final AutoCodec<T> codec;

        public Automatic(Class<T> cls) {
            this.codec = ConfigAutoCodec.of(cls, creator);
            this.cls = cls;
        }

        @Override
        public ConfigObject.Automatic<T> build() {
            if (id == null || type == null || codec == null || creator == null)
                throw new UnsupportedOperationException("ConfigBuilder incomplete! You must define a name, type, codec, and creator.");
            return new ConfigObject.Automatic<>(fileLocation, id, type, codec, creator, onUpdate, presetGetter, watch, null, Platform.getEnv() == EnvType.CLIENT ? ConfigAutoCodec.performFieldSearch(cls) : null);
        }
    }
}
