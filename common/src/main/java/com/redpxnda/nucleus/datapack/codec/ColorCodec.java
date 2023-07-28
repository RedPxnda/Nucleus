package com.redpxnda.nucleus.datapack.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.redpxnda.nucleus.Nucleus;
import net.minecraft.util.FastColor;

import java.util.List;
import java.util.stream.Stream;

public class ColorCodec implements Codec<Integer> {
    public static final ColorCodec INSTANCE = new ColorCodec();

    protected ColorCodec() {}

    @Override
    public <T> DataResult<Pair<Integer, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<String> potentialString = ops.getStringValue(input);
        if (potentialString.result().isPresent()) {
            String hex = potentialString.result().get();
            int color;
            if (hex.isEmpty()) color = FastColor.ARGB32.color(255, 255, 255, 255);
            else {
                long longValue = Long.parseLong(hex, 16);
                color = (int) (longValue & 0xffffffffL);
            }

            return DataResult.success(Pair.of(color, input));
        }

        DataResult<Stream<T>> potentialStream = ops.getStream(input);
        if (potentialStream.result().isPresent()) {
            List<Integer> ints = potentialStream.result().get()
                    .map(element -> {
                        return ops.getNumberValue(element)
                                .getOrThrow(false, s -> Nucleus.LOGGER.error("Failed to create number from '" + element + "' for use in a color! -> " + s)).intValue();
                    }).toList();
            Integer color = null;
            switch (ints.size()) {
                case 3 -> color = FastColor.ARGB32.color(255, ints.get(0), ints.get(1), ints.get(2));
                case 4 -> color = FastColor.ARGB32.color(ints.get(3), ints.get(0), ints.get(1), ints.get(2));
            }
            return color == null ?
                    DataResult.error(() -> "RGB/RGBA colors must contain exactly 3 or 4 colors!") :
                    DataResult.success(Pair.of(color, input));
        }
        return DataResult.error(() -> "Could not create a color from ColorCodec! Not a valid format -> " + input);
    }

    @Override
    public <T> DataResult<T> encode(Integer input, DynamicOps<T> ops, T prefix) {
        return DataResult.success(ops.createInt(input));
    }
}
