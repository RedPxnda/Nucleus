package com.redpxnda.nucleus.datapack.json;

import com.ezylang.evalex.data.EvaluationValue;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.datapack.codec.MiscCodecs;
import com.redpxnda.nucleus.datapack.json.types.Evaluable;
import com.redpxnda.nucleus.math.AxisD;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.math.ParticleShaper;
import com.redpxnda.nucleus.util.Point;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.*;

import static com.redpxnda.nucleus.registry.NucleusRegistries.loc;

public class JsonParticleShaping {
    private static final BiMap<ResourceLocation, ShaperType<?>> shaperRegistry = HashBiMap.create();
    public static final Codec<Shaper> codec = ShaperType.CODEC.dispatch("type", Shaper::type, ShaperType::codec);
    public static final Codec<StoringParticleShaper> completeCodec = RecordCodecBuilder.create(inst -> inst.group(
            MiscCodecs.array(ParticleTypes.CODEC, ParticleOptions[]::new).fieldOf("particles").forGetter(ParticleShaper.Combo::getParticles),
            codec.fieldOf("shaper").forGetter(i -> i.shaper),
            Codec.BOOL.optionalFieldOf("syncToClient", false).forGetter(i -> i.syncToClient),
            AxisD.Codecs.all.optionalFieldOf("transformation", new Quaterniond()).forGetter(ParticleShaper::getTransformation),
            ResourceLocation.CODEC.optionalFieldOf("cacheId").forGetter(i -> i.cacheId),
            Codec.either(Codec.DOUBLE, MiscCodecs.array(Evaluable.Codecs.STRING_ONLY, Evaluable[]::new)).optionalFieldOf("motion", Either.left(0d)).forGetter(i -> i.motion)
    ).apply(inst, (p, s, c, t, ch, m) -> new StoringParticleShaper(p, s, t, c, ch, m)));
    public static <T extends Shaper> ShaperType<T> register(ResourceLocation loc, ShaperType<T> type) {
        shaperRegistry.put(loc, type);
        return type;
    }

    public static ShaperType<Polygon> POLYGON = register(loc("polygon"), Polygon.type);
    public static ShaperType<Bezier> BEZIER = register(loc("bezier"), Bezier.type);
    public static ShaperType<Circle> CIRCLE = register(loc("circle"), Circle.type);
    public static ShaperType<Sphere> SPHERE = register(loc("sphere"), Sphere.type);
    public static ShaperType<Parametric> PARAMETRIC = register(loc("parametric"), Parametric.type);
    public static ShaperType<ParametricSurface> PARAMETRIC_SURFACE = register(loc("parametric_surface"), ParametricSurface.type);
    public static ShaperType<Explicit> EQUATION = register(loc("equation"), Explicit.type);
    public static ShaperType<ExplicitSurface> EQUATION3D = register(loc("equation_3d"), ExplicitSurface.type);
    public static ShaperType<XYRelation> XYREL = register(loc("xy_relation"), XYRelation.type);
    public static ShaperType<XYZRelation> XYZREL = register(loc("xyz_relation"), XYZRelation.type);

    public record Polygon(Double[][] shape, int max, int inc) implements Shaper {
        private static final Codec<Polygon> codec = RecordCodecBuilder.create(inst -> inst.group(
                MiscCodecs.array(MiscCodecs.array(Codec.DOUBLE, Double[]::new), Double[][]::new).fieldOf("points").forGetter(i -> i.shape),
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
            return ParticleShaper.polygon(Arrays.stream(shape).map(ArrayUtils::toPrimitive).toArray(double[][]::new), null, max, inc);
        }
    }
    public record Bezier(Double[][] points, double inc) implements Shaper {
        private static final Codec<Bezier> codec = RecordCodecBuilder.create(inst -> inst.group(
                MiscCodecs.array(MiscCodecs.array(Codec.DOUBLE, Double[]::new), Double[][]::new).fieldOf("points").forGetter(i -> i.points),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, Bezier::new));
        private static final ShaperType<Bezier> type = () -> codec;

        @Override
        public ShaperType<Bezier> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.bezier(Arrays.stream(points).map(ArrayUtils::toPrimitive).toArray(double[][]::new), null, inc);
        }
    }
    public record Circle(double radius, double inc, double startHeight, double maxHeight, double hInc) implements Shaper {
        private static final Codec<Circle> codec = RecordCodecBuilder.create(inst -> inst.group(
                Codec.DOUBLE.fieldOf("max").forGetter(i -> i.radius),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc),
                Codec.DOUBLE.optionalFieldOf("startHeight", 0d).forGetter(i -> i.startHeight),
                Codec.DOUBLE.optionalFieldOf("maxHeight", 1d).forGetter(i -> i.maxHeight),
                Codec.DOUBLE.optionalFieldOf("heightIncrement", 0.1d).forGetter(i -> i.hInc)
        ).apply(inst, Circle::new));
        private static final ShaperType<Circle> type = () -> codec;

        @Override
        public ShaperType<Circle> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.cylinder(null, radius, inc, startHeight, maxHeight, hInc);
        }
    }
    public record Sphere(double radius, double inc) implements Shaper {
        private static final Codec<Sphere> codec = RecordCodecBuilder.create(inst -> inst.group(
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
            return ParticleShaper.sphere(null, radius, inc);
        }
    }
    public record Parametric(Evaluable x, Evaluable y, Evaluable z, MiscCodecs.DoubleRange domain, double inc) implements Shaper {
        private static final Codec<Parametric> codec = RecordCodecBuilder.create(inst -> inst.group(
                Evaluable.Codecs.STRING_ONLY.fieldOf("x").forGetter(i -> i.x),
                Evaluable.Codecs.STRING_ONLY.fieldOf("y").forGetter(i -> i.y),
                Evaluable.Codecs.STRING_ONLY.fieldOf("z").forGetter(i -> i.z),
                MiscCodecs.DOUBLE_RANGE.fieldOf("domain").forGetter(i -> i.domain),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, Parametric::new));
        private static final ShaperType<Parametric> type = () -> codec;

        @Override
        public ShaperType<Parametric> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.Combo.createCombo(null, (s, t) -> {
                s.spawn(x.doubleValue("t", t), y.doubleValue("t", t), z.doubleValue("t", t));
            }, domain.max(), domain.min(), inc);
        }
    }
    public record ParametricSurface(Evaluable x, Evaluable y, Evaluable z, MiscCodecs.DoubleRange uDomain, MiscCodecs.DoubleRange vDomain, double inc) implements Shaper {
        private static final Codec<ParametricSurface> codec = RecordCodecBuilder.create(inst -> inst.group(
                Evaluable.Codecs.STRING_ONLY.fieldOf("x").forGetter(i -> i.x),
                Evaluable.Codecs.STRING_ONLY.fieldOf("y").forGetter(i -> i.y),
                Evaluable.Codecs.STRING_ONLY.fieldOf("z").forGetter(i -> i.z),
                MiscCodecs.DOUBLE_RANGE.fieldOf("uDomain").forGetter(i -> i.uDomain),
                MiscCodecs.DOUBLE_RANGE.fieldOf("vDomain").forGetter(i -> i.vDomain),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, ParametricSurface::new));
        private static final ShaperType<ParametricSurface> type = () -> codec;

        @Override
        public ShaperType<ParametricSurface> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.Combo.createCombo(null, (s, u) -> {
                for (double v = vDomain.min(); v < vDomain.max(); v+=inc) {
                    s.spawn(x.with("v", v).doubleValue("u", u), y.with("v", v).doubleValue("u", u), z.with("v", v).doubleValue("u", u));
                }
            }, uDomain.max(), uDomain.min(), inc);
        }
    }
    public record Explicit(List<Evaluable> evals, MiscCodecs.DoubleRange domain, MiscCodecs.DoubleRange range, double inc, double startHeight, double maxHeight, double hInc) implements Shaper {
        private static final Codec<Explicit> codec = RecordCodecBuilder.create(inst -> inst.group(
                Evaluable.Codecs.ROOTABLE.listOf().fieldOf("equations").forGetter(i -> i.evals),
                MiscCodecs.DOUBLE_RANGE.fieldOf("domain").forGetter(i -> i.domain),
                MiscCodecs.DOUBLE_RANGE.fieldOf("range").forGetter(i -> i.range),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc),
                Codec.DOUBLE.optionalFieldOf("startHeight", 0d).forGetter(i -> i.startHeight),
                Codec.DOUBLE.optionalFieldOf("maxHeight", 1d).forGetter(i -> i.maxHeight),
                Codec.DOUBLE.optionalFieldOf("heightIncrement", 0.1d).forGetter(i -> i.hInc)
        ).apply(inst, Explicit::new));
        private static final ShaperType<Explicit> type = () -> codec;

        @Override
        public ShaperType<Explicit> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.Combo.createCombo(null, (s, x) -> {
                for (Evaluable eval : evals) {
                    eval.with("x", x);
                    double res = MathUtil.solveBrent(y -> eval.with("y", y).evaluate().getNumberValue().doubleValue(), range.min(), range.max(), 1e-9, 100);
                    if (!Double.isNaN(res)) {
                        for (double i = startHeight; i < maxHeight; i+=hInc) {
                            s.spawn(x, i, res);
                        }
                    }
                }
            }, domain.max(), domain.min(), inc);
        }
    }
    public record ExplicitSurface(List<Evaluable> evals, MiscCodecs.DoubleRange xDomain, MiscCodecs.DoubleRange yDomain, MiscCodecs.DoubleRange range, double inc) implements Shaper {
        private static final Codec<ExplicitSurface> codec = RecordCodecBuilder.create(inst -> inst.group(
                Evaluable.Codecs.ROOTABLE.listOf().fieldOf("equations").forGetter(i -> i.evals),
                MiscCodecs.DOUBLE_RANGE.fieldOf("xDomain").forGetter(i -> i.xDomain),
                MiscCodecs.DOUBLE_RANGE.fieldOf("yDomain").forGetter(i -> i.yDomain),
                MiscCodecs.DOUBLE_RANGE.fieldOf("range").forGetter(i -> i.range),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, ExplicitSurface::new));
        private static final ShaperType<ExplicitSurface> type = () -> codec;

        @Override
        public ShaperType<ExplicitSurface> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.Combo.createCombo(null, (s, x) -> {
                for (double y = yDomain.min(); y < yDomain.max(); y+=inc) {
                    for (Evaluable eval : evals) {
                        eval.with("x", x).with("y", y);
                        double res = MathUtil.solveBrent(z -> eval.with("z", z).evaluate().getNumberValue().doubleValue(), range.min(), range.max(), 1e-9, 100);
                        if (!Double.isNaN(res))
                            s.spawn(x, y, res);
                    }
                }
            }, xDomain.max(), xDomain.min(), inc);
        }
    }
    public record XYRelation(List<Evaluable> evals, double max, double min, double inc) implements Shaper {
        private static final Codec<XYRelation> codec = RecordCodecBuilder.create(inst -> inst.group(
                Evaluable.Codecs.STRING_ONLY.listOf().fieldOf("relations").forGetter(i -> i.evals),
                Codec.DOUBLE.fieldOf("max").forGetter(i -> i.max),
                Codec.DOUBLE.fieldOf("min").forGetter(i -> i.min),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, XYRelation::new));
        private static final ShaperType<XYRelation> type = () -> codec;

        @Override
        public ShaperType<XYRelation> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.Combo.createCombo(null, (s, x) -> {
                for (Evaluable eval : evals) {
                    EvaluationValue val = eval.evaluate("x", x);
                    if (!val.isNumberValue()) continue;
                    s.spawn(x, val.getNumberValue().doubleValue());
                }
            }, max, min, inc);
        }
    }
    public record XYZRelation(List<Evaluable> evals, double xMax, double xMin, double yMax, double yMin, double inc) implements Shaper {
        private static final Codec<XYZRelation> codec = RecordCodecBuilder.create(inst -> inst.group(
                Evaluable.Codecs.STRING_ONLY.listOf().fieldOf("relations").forGetter(i -> i.evals),
                Codec.DOUBLE.fieldOf("xMax").forGetter(i -> i.xMax),
                Codec.DOUBLE.fieldOf("xMin").forGetter(i -> i.xMin),
                Codec.DOUBLE.fieldOf("yMax").forGetter(i -> i.yMax),
                Codec.DOUBLE.fieldOf("yMin").forGetter(i -> i.yMin),
                Codec.DOUBLE.fieldOf("increment").forGetter(i -> i.inc)
        ).apply(inst, XYZRelation::new));
        private static final ShaperType<XYZRelation> type = () -> codec;

        @Override
        public ShaperType<XYZRelation> type() {
            return type;
        }

        @Override
        public ParticleShaper setup() {
            return ParticleShaper.Combo.createCombo(null, (s, x) -> {
                for (double y = yMin; y < yMax; y+=inc) {
                    for (Evaluable eval : evals) {
                        EvaluationValue val = eval.with("y", y).evaluate("x", x);
                        if (!val.isNumberValue()) continue;
                        s.spawn(x, val.getNumberValue().doubleValue());
                    }
                }
            }, xMax, xMin, inc);
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

    public static class StoringParticleShaper extends ParticleShaper.Combo {
        public final Shaper shaper;
        public final boolean syncToClient;
        public final Optional<ResourceLocation> cacheId;
        public final Either<Double, Evaluable[]> motion;

        public StoringParticleShaper(ParticleOptions[] options, Shaper shaper, Quaterniond transformation, boolean syncToClient, Optional<ResourceLocation> cacheId, Either<Double, Evaluable[]> motion) {
            super(options, shaper.setup());
            this.shaper = shaper;
            this.inheritT = transformation;
            this.syncToClient = syncToClient;
            this.cacheId = cacheId;
            if (cacheId.isPresent())
                this.cacher(new SimpleShaperCache());

            this.motion = motion;
            if (motion.left().isPresent()) {
                double d = motion.left().get();
                this.motion(v -> new Vector3d(v.x*d, v.y*d, v.z*d));
            } else {
                Evaluable[] e = motion.right().get();
                this.motion(v -> new Vector3d(
                    e[0].with(v).doubleValue(),
                    e[1].with(v).doubleValue(),
                    e[2].with(v).doubleValue()
                ));
            }
        }
    }
    public static class SimpleShaperCache extends ArrayList<Point> implements ParticleShaper.Cacher {
        private boolean isCached = false;

        @Override
        public void put(Vector3d pos, Vector3d mot) {
            this.add(new Point(pos, mot));
        }

        @Override
        public boolean has(ParticleShaper shaper) {
            return isCached;
        }

        @Override
        public void setHas(boolean bl) {
            isCached = bl;
        }

        @Override
        public void call(ParticleShaper shaper) {
            forEach(v -> v.spawn(shaper));
        }
    }
}
