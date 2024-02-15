package com.redpxnda.nucleus.codec.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.util.Color;
import org.slf4j.Logger;

import java.util.List;

public class ColorCodec implements Codec<Color> {
    private static final Logger LOGGER = Nucleus.getLogger();

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
            int r = ops.getNumberValue(map.get("r")).getOrThrow(false, s -> LOGGER.error("Invalid number used for color's r value! '" + map.get("r") + "'")).intValue();
            int g = ops.getNumberValue(map.get("g")).getOrThrow(false, s -> LOGGER.error("Invalid number used for color's g value! '" + map.get("g") + "'")).intValue();
            int b = ops.getNumberValue(map.get("b")).getOrThrow(false, s -> LOGGER.error("Invalid number used for color's b value! '" + map.get("b") + "'")).intValue();
            int a = ops.getNumberValue(map.get("a"), 255).intValue();
            return DataResult.success(Pair.of(new Color(r, g, b, a), input));
        }

        DataResult<List<Integer>> listResult = Codec.INT.listOf().parse(ops, input);
        if (listResult.result().isPresent()) {
            List<Integer> list = listResult.result().get();

            if (list.size() > 4 || list.size() < 3)
                return DataResult.error(() -> "Invalid array size used for a color! Must be either 3 (red, green, blue- alpha defaulting to 255) or 4! (red, green, blue, alpha)");

            int alpha = list.size() > 3 ? list.get(3) : 255;
            return DataResult.success(Pair.of(new Color(list.get(0), list.get(1), list.get(2), alpha), input));
        }

        return DataResult.error(() -> "Could not create a color from ColorCodec! Not a valid format -> " + input);
    }

    @Override
    public <T> DataResult<T> encode(Color input, DynamicOps<T> ops, T prefix) {
        return DataResult.success(ops.createString(input.hex()));
    }
}
