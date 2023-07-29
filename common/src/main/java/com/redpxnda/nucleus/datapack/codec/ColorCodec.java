package com.redpxnda.nucleus.datapack.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.util.Color;
import net.minecraft.util.FastColor;

import java.util.List;
import java.util.stream.Stream;

public class ColorCodec implements Codec<Color> {
    public static final ColorCodec INSTANCE = new ColorCodec();

    protected ColorCodec() {}

    @Override
    public <T> DataResult<Pair<Color, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<String> potentialString = ops.getStringValue(input);
        if (potentialString.result().isPresent()) {
            String hex = potentialString.result().get();
            return DataResult.success(Pair.of(new Color(hex), input));
        }

        DataResult<MapLike<T>> potentialMap = ops.getMap(input);
        if (potentialMap.result().isPresent()) {
            MapLike<T> map = potentialMap.result().get();
            int r = ops.getNumberValue(map.get("r")).getOrThrow(false, s -> Nucleus.LOGGER.error("Invalid number used for color's r value! '" + map.get("r") + "'")).intValue();
            int g = ops.getNumberValue(map.get("g")).getOrThrow(false, s -> Nucleus.LOGGER.error("Invalid number used for color's g value! '" + map.get("g") + "'")).intValue();
            int b = ops.getNumberValue(map.get("b")).getOrThrow(false, s -> Nucleus.LOGGER.error("Invalid number used for color's b value! '" + map.get("b") + "'")).intValue();
            int a = ops.getNumberValue(map.get("a"), 255).intValue();
            return DataResult.success(Pair.of(new Color(r, g, b, a), input));
        }

        return DataResult.error(() -> "Could not create a color from ColorCodec! Not a valid format -> " + input);
    }

    @Override
    public <T> DataResult<T> encode(Color input, DynamicOps<T> ops, T prefix) {
        return DataResult.success(ops.createString(input.hex()));
    }
}
