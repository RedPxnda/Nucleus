package com.redpxnda.nucleus.capability.doubles;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.datapack.codec.InterfaceDispatcher;
import com.redpxnda.nucleus.datapack.codec.MiscCodecs;
import com.redpxnda.nucleus.util.JsonUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientCapabilityListener {
    public static final Map<String, RenderingMode.Creator> renderingModes = new HashMap<>();
    public static final InterfaceDispatcher<RenderingMode.Creator> renderingModeDispatcher = InterfaceDispatcher.of(renderingModes, "mode");
    public static final Map<String, RenderingPredicate.Creator> renderingPredicates = new HashMap<>();
    public static final InterfaceDispatcher<RenderingPredicate.Creator> renderingPredicateDispatcher = InterfaceDispatcher.of(renderingPredicates, "type");
    public static final Map<String, InterpolateMode.Creator> interpolateModes = new HashMap<>();
    public static final InterfaceDispatcher<InterpolateMode.Creator> interpolateModeDispatcher = InterfaceDispatcher.of(interpolateModes, "mode");

    public static final Map<String, RenderingMode> renderers = new HashMap<>();

    static {
        Class<?> classLoading = RenderingMode.Bar.class;

        renderingModes.put("progress_bar", element -> RenderingMode.Bar.codec.parse(JsonOps.INSTANCE, element)
                .getOrThrow(false, s -> Nucleus.LOGGER.error("Failed to parse progress_bar rendering mode for a simple capability! -> " + s)));

        renderingPredicates.put("always", element -> RenderingPredicate.ALWAYS_PREDICATE);
        renderingPredicates.put("never", element -> RenderingPredicate.NEVER_PREDICATE);
        renderingPredicates.put("after_modified", element -> {
            if (element instanceof JsonObject obj)
                return MiscCodecs.quickParse(obj, RenderingPredicate.RecentModificationRP.codec, s -> Nucleus.LOGGER.error("Failed to parse 'after_modified' rendering predicate for simple capability! -> " + s));
            else return new RenderingPredicate.RecentModificationRP(5f, 0.25f, 0.25f);
        });

        interpolateModes.put("none", e -> InterpolateMode.NONE);
        interpolateModes.put("lerp", e -> InterpolateMode.LERP);
        interpolateModes.put("cosine", e -> InterpolateMode.COS);
        interpolateModes.put("easeIn", e -> new InterpolateMode.EaseIn(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
        interpolateModes.put("easeOut", e -> new InterpolateMode.EaseOut(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
        interpolateModes.put("easeInOut", e -> new InterpolateMode.EaseInOut(JsonUtil.getIfObject(e, obj -> JsonUtil.getOrElse(obj, "amplifier", 2f), 2f)));
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
}