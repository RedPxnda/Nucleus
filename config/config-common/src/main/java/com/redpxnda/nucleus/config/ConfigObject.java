package com.redpxnda.nucleus.config;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.ops.JsoncOps;
import com.redpxnda.nucleus.config.preset.ConfigPreset;
import com.redpxnda.nucleus.util.json.JsoncElement;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigObject<T> {
    public final Path path;
    public final String name;
    public final ConfigType type;
    public final Codec<T> codec;
    public final Supplier<T> defaultCreator;
    public final @Nullable Consumer<T> onUpdate;
    public final @Nullable Function<T, ConfigPreset<T, ?>> presetGetter;
    public final boolean watch;
    protected @Nullable T instance;

    /**
     * @param path           the path to the config
     * @param name           the name for the config
     * @param type           the type of config
     * @param codec          the codec used to (de)serialize the config
     * @param defaultCreator a supplier to create the default/empty version of this config
     * @param onUpdate       handler for when the config is updated
     * @param presetGetter
     * @param watch
     * @param instance       handler instance, null before first read
     */
    public ConfigObject(Path path, String name, ConfigType type, Codec<T> codec, Supplier<T> defaultCreator, @Nullable Consumer<T> onUpdate, @Nullable Function<T, ConfigPreset<T, ?>> presetGetter, boolean watch, @Nullable T instance) {
        this.path = path;
        this.name = name;
        this.type = type;
        this.codec = codec;
        this.defaultCreator = defaultCreator;
        this.onUpdate = onUpdate;
        this.presetGetter = presetGetter;
        this.watch = watch;
        this.instance = instance;
    }

    public File getFile() {
        return path.resolve(name + ".jsonc").toFile();
    }
    public File getBackupFile(int num) {
        return path.resolve(name + "-backup-" + num + ".jsonc").toFile();
    }

    public <C> C serialize(DynamicOps<C> ops) {
        return codec.encodeStart(ops, instance)
                .getOrThrow(false, s -> Nucleus.LOGGER.error("Failed to encode config '{}'! -> {}", name, s));
    }

    protected void attemptParse(String data) {
        JsonElement json = Nucleus.GSON.fromJson(data, JsonElement.class);
        instance = codec.parse(JsonOps.INSTANCE, json)
                .getOrThrow(false, s -> Nucleus.LOGGER.error("Failed to parse json for config '{}'! -> {}", name, s));

        if (presetGetter != null) {
            var preset = presetGetter.apply(instance);
            String presetName = preset.getEntry() == null ? "none" : preset.getEntry().name();
            T val = preset.consume();
            if (val != null) {
                instance = val;
                Nucleus.LOGGER.info("Loaded preset '{}' for config '{}'.", presetName, name);
            }
        }

        if (onUpdate != null) onUpdate.accept(instance);
    }

    public void resetToDefault() {
        instance = defaultCreator.get();
        if (onUpdate != null) onUpdate.accept(instance);
    }

    /**
     * Load this config with manually inputted data
     */
    public void load(String data) {
        try {
            attemptParse(data);
        } catch (Exception e) {
            Nucleus.LOGGER.warn("Failed to read manually inputted data for config '" + name + "'!", e);
            resetToDefault();
        }
    }

    /**
     * @return null if failed, or the string data if successful
     */
    public String load() {
        File file = getFile();
        if (file.exists()) {
            try {
                String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                attemptParse(data);
            } catch (Exception e) {
                Nucleus.LOGGER.warn("Failed to read data for config '" + name + "'. Creating backup...", e);

                int num = 0;
                while (getBackupFile(num).exists()) num++;

                try {
                    FileUtils.copyFile(file, getBackupFile(num));
                } catch (Exception ex) {
                    Nucleus.LOGGER.warn("Failed to create backup for config '" + name + "'. Deleting instead...", ex);
                    if (!file.delete()) Nucleus.LOGGER.warn("Deletion failed for config '{}'!", name);
                }

                resetToDefault();
            }
        } else {
            resetToDefault();
        }
        return null;
    }

    public void save() {
        if (instance == null) {
            Nucleus.LOGGER.warn("Attempted to save null config instance '{}'! Ignoring...", name);
            return;
        }
        try {
            JsoncElement json = serialize(JsoncOps.INSTANCE);
            FileUtils.write(getFile(), json.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Nucleus.LOGGER.warn("Failed to save data for config '" + name + "'. Ignoring...", e);
        }
    }

    public @Nullable T instance() {
        return instance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ConfigObject<?> config))
            return false;
        return config.path.toString().equals(path.toString());
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "ConfigObject[" +
                "path=" + path + ", " +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "codec=" + codec + ", " +
                "defaultCreator=" + defaultCreator + ", " +
                "onUpdate=" + onUpdate + ", " +
                "instance=" + instance + ']';
    }

    public static class Automatic<T> extends ConfigObject<T> {
        public Automatic(Path path, String name, ConfigType type, Codec<T> codec, Supplier<T> defaultCreator, @Nullable Consumer<T> onUpdate, @Nullable Function<T, ConfigPreset<T, ?>> presetGetter, boolean watch, @Nullable T instance) {
            super(path, name, type, codec, defaultCreator, onUpdate, presetGetter, watch, instance);
        }
    }
}
