package com.redpxnda.nucleus.datapack.codec;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static com.redpxnda.nucleus.registry.NucleusRegistries.loc;

public class DoubleSupplier {
    public static final BiMap<ResourceLocation, Type<?>> suppliers = HashBiMap.create();
    public static final Codec<Instance> DISPATCH = Type.CODEC.dispatch("type", Instance::type, Type::codec);
    public static final Codec<Instance> CODEC = Codec.either(
            Codec.DOUBLE,
            DISPATCH
    ).xmap(e -> {
        if (e.left().isPresent())
            return new Basic(e.left().get());
        else
            return e.right().get();
    }, Either::right);
    public static final Codec<Supplier<Double>> SUP_CODEC = CODEC.flatComapMap(i -> i, sup -> {
       if (sup instanceof Instance i)
           return DataResult.success(i);
       else
           return DataResult.error("Failed to convert Supplier to DoubleSupplier Instance.");
    });
    public static <T extends Instance> Type<T> register(ResourceLocation location, Type<T> type) {
        suppliers.put(location, type);
        return type;
    }

    private static final Type<Random> RANDOM = register(loc("random"), Random.type);
    private static final Type<Basic> BASIC = register(loc("basic"), Basic.type);

    public record Random(double min, double max) implements Instance {
        public static final Codec<Random> codec = RecordCodecBuilder.create(inst -> inst.group(
                Codec.DOUBLE.fieldOf("min").forGetter(Random::min),
                Codec.DOUBLE.fieldOf("max").forGetter(Random::max)
        ).apply(inst, Random::new));
        public static final Type<Random> type = () -> codec;

        @Override
        public Type<Random> type() {
            return type;
        }

        @Override
        public Double get() {
            return Math.random() * (max - min) + min;
        }
    }
    public record Basic(double val) implements Instance {
        public static final Type<Basic> type = () -> Codec.DOUBLE.xmap(Basic::new, b -> b.val);

        @Override
        public Type<Basic> type() {
            return type;
        }

        @Override
        public Double get() {
            return val;
        }
    }

    public interface Instance extends Supplier<Double> {
        Type<?> type();
    }
    public interface Type<T extends Instance> {
        Codec<Type<?>> CODEC = ResourceLocation.CODEC.xmap(suppliers::get, suppliers.inverse()::get);

        Codec<T> codec();
    }
}
