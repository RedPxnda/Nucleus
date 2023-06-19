package com.redpxnda.nucleus.datapack.json;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.ParseException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.datapack.codec.ArrayCodec;
import com.redpxnda.nucleus.datapack.json.types.Evaluable;
import com.redpxnda.nucleus.math.AxisD;
import com.redpxnda.nucleus.util.ParticleShaper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Quaterniond;
import org.slf4j.Logger;

import java.util.*;

import static com.redpxnda.nucleus.registry.NucleusRegistries.loc;

public class JsonParticleShaping {
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final BiMap<ResourceLocation, ShaperType<?>> shaperRegistry = HashBiMap.create();
    public static final Codec<Shaper> codec = ShaperType.CODEC.dispatch("type", Shaper::type, ShaperType::codec);
    public static final Codec<ParticleShaper> completeCodec = RecordCodecBuilder.create(inst -> inst.group(
            codec.fieldOf("shaper").forGetter(i -> ((StoringParticleShaper) i).shaper),
            AxisD.Codecs.all.optionalFieldOf("transformation", new Quaterniond()).forGetter(ParticleShaper::getTransformation)
    ).apply(inst, (s, t) -> new StoringParticleShaper(s).transform(t)));
    public static <T extends Shaper> ShaperType<T> register(ResourceLocation loc, ShaperType<T> type) {
        shaperRegistry.put(loc, type);
        return type;
    }

    public static ShaperType<Polygon> POLYGON = register(loc("polygon"), Polygon.type);
    public static ShaperType<Bezier> BEZIER = register(loc("bezier"), Bezier.type);
    public static ShaperType<Circle> CIRCLE = register(loc("circle"), Circle.type);
    public static ShaperType<Sphere> SPHERE = register(loc("sphere"), Sphere.type);
    public static ShaperType<Equation> EQUATION = register(loc("equation"), Equation.type);

    public record Polygon(Double[][] shape, ParticleOptions options, int max, int inc) implements Shaper {
        private static final Codec<Polygon> codec = RecordCodecBuilder.create(inst -> inst.group(
                ArrayCodec.of(ArrayCodec.of(Codec.DOUBLE, Double[]::new), Double[][]::new).fieldOf("points").forGetter(i -> i.shape),
                ParticleTypes.CODEC.fieldOf("particle").forGetter(i -> i.options),
                Codec.INT.fieldOf("max").forGetter(i -> i.max),
                Codec.INT.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, Polygon::new));
        private static final ShaperType<Polygon> type = () -> codec;

        @Override
        public ShaperType<Polygon> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.polygon(Arrays.stream(shape).map(ArrayUtils::toPrimitive).toArray(double[][]::new), options, max, inc);
        }
    }
    public record Bezier(Double[][] points, ParticleOptions options, double inc) implements Shaper {
        private static final Codec<Bezier> codec = RecordCodecBuilder.create(inst -> inst.group(
                ArrayCodec.of(ArrayCodec.of(Codec.DOUBLE, Double[]::new), Double[][]::new).fieldOf("points").forGetter(i -> i.points),
                ParticleTypes.CODEC.fieldOf("particle").forGetter(i -> i.options),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, Bezier::new));
        private static final ShaperType<Bezier> type = () -> codec;

        @Override
        public ShaperType<Bezier> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.bezier(Arrays.stream(points).map(ArrayUtils::toPrimitive).toArray(double[][]::new), options, inc);
        }
    }
    public record Circle(ParticleOptions options, double radius, double inc) implements Shaper {
        private static final Codec<Circle> codec = RecordCodecBuilder.create(inst -> inst.group(
                ParticleTypes.CODEC.fieldOf("particle").forGetter(i -> i.options),
                Codec.DOUBLE.fieldOf("max").forGetter(i -> i.radius),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, Circle::new));
        private static final ShaperType<Circle> type = () -> codec;

        @Override
        public ShaperType<Circle> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.circle(options, radius, inc);
        }
    }
    public record Sphere(ParticleOptions options, double radius, double inc) implements Shaper {
        private static final Codec<Sphere> codec = RecordCodecBuilder.create(inst -> inst.group(
                ParticleTypes.CODEC.fieldOf("particle").forGetter(i -> i.options),
                Codec.DOUBLE.fieldOf("max").forGetter(i -> i.radius),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, Sphere::new));
        private static final ShaperType<Sphere> type = () -> codec;

        @Override
        public ShaperType<Sphere> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.sphere(options, radius, inc);
        }
    }
    public record Equation(List<Evaluable> evals, ParticleOptions options, double max, double min, double inc) implements Shaper {
        private static final Codec<Equation> codec = RecordCodecBuilder.create(inst -> inst.group(
                Evaluable.Codecs.STRING_ONLY.listOf().fieldOf("equations").forGetter(i -> i.evals),
                ParticleTypes.CODEC.fieldOf("particle").forGetter(i -> i.options),
                Codec.DOUBLE.fieldOf("max").forGetter(i -> i.max),
                Codec.DOUBLE.fieldOf("min").forGetter(i -> i.min),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, Equation::new));
        private static final ShaperType<Equation> type = () -> codec;

        @Override
        public ShaperType<Equation> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.create(options, (s, x) -> {
                for (Evaluable eval : evals) {
                    try {
                        EvaluationValue val = eval.evaluate("x", x);
                        if (!val.isNumberValue()) continue;
                        s.spawn(x, val.getNumberValue().doubleValue());
                    } catch (EvaluationException | ParseException e) {
                        LOGGER.error("Failed to evaluate evaluable '{}'!", eval.getRaw());
                        throw new RuntimeException(e);
                    }
                }
            }, max, min, inc);
        }
    }
    public interface Shaper {
        ShaperType<?> type();
        ParticleShaper setup();
    }
    public interface ShaperType<T extends Shaper> {
        Codec<ShaperType<?>> CODEC = ResourceLocation.CODEC.xmap(shaperRegistry::get, shaperRegistry.inverse()::get);

        Codec<T> codec();
    }

    public static class StoringParticleShaper extends ParticleShaper {
        private final Shaper shaper;

        public StoringParticleShaper(Shaper shaper) {
            super(shaper.setup());
            this.shaper = shaper;
        }
    }
}
