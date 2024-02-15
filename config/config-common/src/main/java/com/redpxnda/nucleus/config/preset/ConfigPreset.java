package com.redpxnda.nucleus.config.preset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.behavior.CodecBehavior;
import com.redpxnda.nucleus.config.ConfigBuilder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Function;

/**
 * A simple way to allow "presets" for a config. Presets, in this case, are hardcoded instances of your config
 * that provide your user with a way to have quick and easy suggested configurations. For example, maybe you want
 * a special hardmode for your mod, enabled in a config, but still want all the changes of this hardmode to be customizable.
 * This can do exactly that.
 * Presets are defined with {@link ConfigBuilder#presetGetter(Function)}, and evaluated when your config is loaded.
 * If the preset is set(valid names are defined in an enum), the config instance will be replaced with the preset.
 * To define the presets, create an enum implementing {@code ConfigProvider<YourConfigClass>}. The enum names will
 * be the different presets, and the {@link ConfigProvider#getInstance()} method will determine how to get the config
 * instance from the preset.
 *
 * @param <C> the config this preset is for
 * @param <E> the enum holding the valid presets
 */
@CodecBehavior.Override("codecGetter")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigPreset<C, E extends Enum<E> & ConfigProvider<C>> {
    private static final Logger LOGGER = Nucleus.getLogger();

    public static <C, E extends Enum<E> & ConfigProvider<C>> ConfigPreset<C, E> none() {
        return new ConfigPreset<>(null);
    }
    public static <C, E extends Enum<E> & ConfigProvider<C>> ConfigPreset<C, E> of(E entry) {
        return new ConfigPreset<>(entry);
    }

    public static final CodecBehavior.Getter<ConfigPreset> codecGetter = (f, clazz, raw, params, root) -> {
        if (params == null || !(params[1] instanceof Class cls)) {
            LOGGER.warn("Invalid format used for ConfigPreset field. Please specify type parameters as direct classes without type parameters.");
            return Codec.STRING.xmap(s -> ConfigPreset.none(), c -> "none");
        }
        return Codec.STRING.comapFlatMap(s -> {
            if (s.equals("none"))
                return DataResult.success(new ConfigPreset(null));
            try {
                Enum entry = Enum.valueOf(cls, s);
                return DataResult.success(new ConfigPreset(entry));
            } catch (IllegalArgumentException ex) {
                return DataResult.error(() -> "Invalid preset name used! Could not find preset named '" + s + "'.", new ConfigPreset(null));
            }
        }, p -> p.entry == null ? "none" : p.entry.name());
    };

    protected @Nullable E entry;

    protected ConfigPreset(@Nullable E entry) {
        this.entry = entry;
    }

    public @Nullable E getEntry() {
        return entry;
    }

    public @Nullable C consume() {
        C result = entry == null ? null : entry.getInstance();
        entry = null;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigPreset<?, ?> that = (ConfigPreset<?, ?>) o;
        return Objects.equals(entry, that.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entry);
    }
}
