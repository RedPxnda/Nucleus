package com.redpxnda.nucleus.config.preset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.codec.auto.AutoCodec;
import com.redpxnda.nucleus.config.ConfigBuilder;
import org.jetbrains.annotations.Nullable;

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
@AutoCodec.Override("codecGetter")
@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigPreset<C, E extends Enum<E> & ConfigProvider<C>> {
    public static <C, E extends Enum<E> & ConfigProvider<C>> ConfigPreset<C, E> none() {
        return new ConfigPreset<>(null);
    }
    public static <C, E extends Enum<E> & ConfigProvider<C>> ConfigPreset<C, E> of(E entry) {
        return new ConfigPreset<>(entry);
    }

    public static final AutoCodec.CodecGetter<ConfigPreset> codecGetter = params -> {
        if (params == null || !(params[1] instanceof Class cls)) {
            Nucleus.LOGGER.warn("Invalid format used for ConfigPreset field. Please specify type parameters as direct classes without type parameters.");
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
}
