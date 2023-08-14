package com.redpxnda.nucleus.datapack.json.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.datapack.codec.InterfaceDispatcher;
import com.redpxnda.nucleus.datapack.codec.MiscCodecs;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.GuiDrawUtil;
import com.redpxnda.nucleus.util.JsonUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientCapabilityListener {
    public static final Map<String, RenderingModeCreator> renderingModes = new HashMap<>();
    public static final InterfaceDispatcher<RenderingModeCreator> renderingModeDispatcher = InterfaceDispatcher.of(renderingModes, "mode");
    public static final Map<String, RenderingPredicateCreator> renderingPredicates = new HashMap<>();
    public static final InterfaceDispatcher<RenderingPredicateCreator> renderingPredicateDispatcher = InterfaceDispatcher.of(renderingPredicates, "type");
    public static final Map<String, InterpolateModeCreator> interpolateModes = new HashMap<>();
    public static final InterfaceDispatcher<InterpolateModeCreator> interpolateModeDispatcher = InterfaceDispatcher.of(interpolateModes, "mode");
    public static final RenderingPredicate ALWAYS_PREDICATE = (v, t) -> true;
    public static final RenderingPredicate NEVER_PREDICATE = (v, t) -> false;

    public static final Map<String, RenderingMode> renderers = new HashMap<>();

    static {
        Class<?> classLoading = BarRenderingMode.class;

        renderingModes.put("progress_bar", element -> BarRenderingMode.codec.parse(JsonOps.INSTANCE, element)
                .getOrThrow(false, s -> Nucleus.LOGGER.error("Failed to parse progress_bar rendering mode for a simple capability! -> " + s)));

        renderingPredicates.put("always", element -> ALWAYS_PREDICATE);
        renderingPredicates.put("never", element -> NEVER_PREDICATE);
        renderingPredicates.put("after_modified", element -> {
            if (element instanceof JsonObject obj)
                return MiscCodecs.quickParse(obj, RecentModificationRP.codec, s -> Nucleus.LOGGER.error("Failed to parse 'after_modified' rendering predicate for simple capability! -> " + s));
            else return new RecentModificationRP(5f, 0.25f, 0.25f);
        });

        interpolateModes.put("none", e -> InterpolateMode.NONE);
        interpolateModes.put("lerp", e -> InterpolateMode.LERP);
        interpolateModes.put("cosine", e -> InterpolateMode.COS);
        interpolateModes.put("easeIn", e -> new EaseIn(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
        interpolateModes.put("easeOut", e -> new EaseOut(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
        interpolateModes.put("easeInOut", e -> new EaseInOut(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
    }

    public static void parseRenderingObject(JsonObject obj, String name) {
        RenderingMode mode = renderingModeDispatcher.dispatcher().createFrom(obj);

        JsonUtil.runIfPresent(obj, "show", show -> mode.predicate = renderingPredicateDispatcher.dispatcher().createFrom(show));
        JsonUtil.runIfPresent(obj, "margin", margin -> mode.margin = margin.getAsInt());
        JsonUtil.runIfPresent(obj, "interpolate", interp -> mode.interpolate = interpolateModeDispatcher.dispatcher().createFrom(interp));
        JsonUtil.runIfPresent(obj, "interpolateTime", time -> mode.interpolateTime = time.getAsFloat());
        JsonUtil.runIfPresent(obj, "adjustInterpolateTarget", bl -> mode.adjustInterpolateTarget = bl.getAsBoolean());

        renderers.put(name, mode);
    }

    @Environment(EnvType.CLIENT)
    public interface RenderingModeCreator {
        RenderingMode createFrom(JsonElement element);
    }

    @Environment(EnvType.CLIENT)
    public interface RenderingPredicateCreator {
        RenderingPredicate createFrom(JsonElement element);
    }

    @Environment(EnvType.CLIENT)
    public interface RenderingPredicate {
        /**
         * @param val value of the double capability in question
         * @param time time of last modification of the double capability
         * @return whether the double capability can render or not
         */
        boolean canRender(double val, long time);
    }

    @Environment(EnvType.CLIENT)
    public interface InterpolateModeCreator {
        InterpolateMode createFrom(JsonElement element);
    }

    @Environment(EnvType.CLIENT)
    public interface InterpolateMode {
        InterpolateMode NONE = (delta, last, current) -> current;
        InterpolateMode LERP = MathUtil::lerp;
        InterpolateMode COS = (delta, last, current) -> {
            double d = (1-Math.cos(delta*Math.PI))/2;
            return MathUtil.lerp(d, last, current);
        };

        double interpolate(float delta, double last, double current);
    }

    @Environment(EnvType.CLIENT)
    public record EaseIn(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            return MathUtil.lerp(Math.pow(delta, amplifier), last, current);
        }
    }
    @Environment(EnvType.CLIENT)
    public record EaseOut(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            return MathUtil.lerp(MathUtil.flip(MathUtil.pow(MathUtil.flip(delta), amplifier)), last, current);
        }
    }
    @Environment(EnvType.CLIENT)
    public record EaseInOut(float amplifier) implements InterpolateMode {
        @Override
        public double interpolate(float delta, double last, double current) {
            float flippedDelta = MathUtil.flip(MathUtil.pow(MathUtil.flip(delta), amplifier));
            float normalDelta = MathUtil.pow(delta, amplifier);
            float finalDelta = MathUtil.lerp(delta, normalDelta, flippedDelta);
            return MathUtil.lerp(finalDelta, last, current);
        }
    }

    @Environment(EnvType.CLIENT)
    public interface AlphaProvider {
        float getAlpha(long time);
    }

    @Environment(EnvType.CLIENT)
    public static class RecentModificationRP implements RenderingPredicate, AlphaProvider {
        public static final Codec<RecentModificationRP> codec = RecordCodecBuilder.create(inst -> inst.group(
                Codec.FLOAT.optionalFieldOf("seconds", 5f).forGetter(r -> r.seconds),
                Codec.FLOAT.optionalFieldOf("fadeIn", 0.25f).forGetter(r -> r.fadeIn),
                Codec.FLOAT.optionalFieldOf("fadeOut", 0.25f).forGetter(r -> r.fadeOut)
        ).apply(inst, RecentModificationRP::new));
        private final float seconds;
        private final float fadeIn;
        private final float fadeOut;
        private boolean hasSetFadeIn = false;
        private long fadeInStart;

        public RecentModificationRP(float seconds, float fadeIn, float fadeOut) {
            this.seconds = seconds;
            this.fadeIn = fadeIn;
            this.fadeOut = fadeOut;
        }

        @Override
        public boolean canRender(double val, long time) {
            long currentTime = Util.getMillis();
            double dif = (currentTime - time) / 1000d;
            return dif < seconds; // didnt inline this stuff for testing purposes
        }

        @Override
        public float getAlpha(long time) {
            long currentTime = Util.getMillis();
            float dif = (currentTime - time) / 1000f;
            if (!hasSetFadeIn) {
                fadeInStart = time;
                hasSetFadeIn = true;
            }
            float inDif = (currentTime - fadeInStart) / 1000f;
            if (inDif < fadeIn)
                return MathUtil.lerp(inDif / fadeIn, 0f, 1f);
            if (dif >= seconds - fadeOut) {
                hasSetFadeIn = false;
                return MathUtil.lerp((dif - (seconds-fadeOut)) / fadeOut, 1f, 0f);
            }
            return 1;
        }
    }

    @Environment(EnvType.CLIENT)
    public static abstract class RenderingMode {
        public @AutoCodec.Ignored RenderingPredicate predicate = ALWAYS_PREDICATE;
        public @AutoCodec.Ignored int margin = 2;
        public @AutoCodec.Ignored InterpolateMode interpolate = InterpolateMode.NONE;
        public @AutoCodec.Ignored float interpolateTime = 1;
        public @AutoCodec.Ignored boolean adjustInterpolateTarget = true; // true enables behavior to prevent jumps when values change in the middle of an interpolation animation

        public abstract void render(double capValue, GuiGraphics graphics, int x, int y, float alpha);

        public abstract int getWidth();
        public abstract int getHeight();
    }

    @Environment(EnvType.CLIENT)
    @AutoCodec.Settings(optionalByDefault = true)
    public static class BarRenderingMode extends RenderingMode {
        public static final Codec<BarRenderingMode> codec = AutoCodec.of(BarRenderingMode.class).codec();

        public int width = 16;
        public int height = 2;
        public @AutoCodec.Mandatory double maxValue;
        public Color filledColor = Color.WHITE;
        public Color emptyColor = Color.GRAY;

        @Override
        public void render(double capValue, GuiGraphics graphics, int x, int y, float alpha) {
            float fillPercent = (float) MathUtil.clamp(capValue/maxValue, 0, 1);
            x-=width/2f;
            float filledX = x + fillPercent*width;

            Color filled = new Color(filledColor);
            filled.w*=alpha;
            Color empty = new Color(emptyColor);
            empty.w*=alpha;

            GuiDrawUtil.fill(graphics, x, y, filledX, y+height, filled);
            GuiDrawUtil.fill(graphics, filledX, y, x + width, y+height, empty);
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }
    }
}