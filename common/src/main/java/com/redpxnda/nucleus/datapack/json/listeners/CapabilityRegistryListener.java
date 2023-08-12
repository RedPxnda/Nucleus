package com.redpxnda.nucleus.datapack.json.listeners;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.redpxnda.nucleus.Nucleus;
import com.redpxnda.nucleus.capability.DoublesCapability;
import com.redpxnda.nucleus.datapack.codec.AutoCodec;
import com.redpxnda.nucleus.datapack.codec.InterfaceDispatcher;
import com.redpxnda.nucleus.util.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapabilityRegistryListener extends SimpleJsonResourceReloadListener {
    public static final Map<String, RenderingModeCreator> renderingModes = new HashMap<>();
    public static final Map<String, RenderingPredicateCreator> renderingPredicates = new HashMap<>();
    public static final InterfaceDispatcher<RenderingModeCreator> renderingModeDispatcher = InterfaceDispatcher.of(renderingModes, "mode");
    public static final InterfaceDispatcher<RenderingPredicateCreator> renderingPredicateDispatcher = InterfaceDispatcher.of(renderingPredicates, "type");

    public static final RenderingPredicate ALWAYS_PREDICATE = (v, t) -> true;
    public static final RenderingPredicate NEVER_PREDICATE = (v, t) -> false;

    public CapabilityRegistryListener() {
        super(Nucleus.GSON, "capabilities");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DoublesCapability.defaultValues.clear();
        DoublesCapability.renderers.clear();
        object.forEach((path, value) -> {
            List<JsonObject> elements = new ArrayList<>();
            if (value instanceof JsonArray array)
                elements.addAll(array.asList().stream().filter(e -> e instanceof JsonObject).map(JsonElement::getAsJsonObject).toList());
            else if (value instanceof JsonObject obj)
                elements.add(obj);
            else
                Nucleus.LOGGER.warn("Found a JSON element used for a simple capability that isn't a JSON object or array!");

            elements.forEach(element -> {
                if (!(element.get("name") instanceof JsonPrimitive prim) || !prim.isString()) {
                    throw new RuntimeException("Key 'name' for simple capability at '" + path + "' is either missing or not a valid string!");
                }
                String name = new ResourceLocation(element.get("name").getAsString()).toString(); // forcing resource location format for compat reasons

                if (element.get("defaultValue") instanceof JsonPrimitive primitive)
                    DoublesCapability.defaultValues.put(name, primitive.getAsDouble());
                if (element.get("rendering") instanceof JsonObject obj) {
                    RenderingMode mode = renderingModeDispatcher.dispatcher().createFrom(obj);

                    JsonElement show = obj.get("show");
                    if (show != null)
                        mode.predicate = renderingPredicateDispatcher.dispatcher().createFrom(show);

                    DoublesCapability.renderers.put(name, mode);
                }
            });
        });
    }

    static {
        var classLoading = BarRenderingMode.class;

        renderingModes.put("progress_bar", element -> BarRenderingMode.codec.parse(JsonOps.INSTANCE, element)
                .getOrThrow(false, s -> Nucleus.LOGGER.error("Failed to parse progress_bar rendering mode for a simple capability! -> " + s)));

        renderingPredicates.put("always", element -> ALWAYS_PREDICATE);
        renderingPredicates.put("never", element -> ALWAYS_PREDICATE);
        renderingPredicates.put("after_modified", element -> {
            if (element instanceof JsonObject obj)
                return new RecentModificationRP(obj.getAsJsonPrimitive("seconds").getAsFloat());
            else return new RecentModificationRP(5f);
        });
    }

    public interface RenderingModeCreator {
        RenderingMode createFrom(JsonElement element);
    }

    public interface RenderingPredicateCreator {
        RenderingPredicate createFrom(JsonElement element);
    }
    public interface RenderingPredicate {
        /**
         * @param val value of the double capability in question
         * @param time time of last modification of the double capability
         * @return whether the double capability can render or not
         */
        boolean canRender(double val, long time);
    }
    public static class RecentModificationRP implements RenderingPredicate {
        public final float seconds;

        public RecentModificationRP(float seconds) {
            this.seconds = seconds;
        }

        @Override
        public boolean canRender(double val, long time) {
            return (Util.getMillis()-time)/1000d > seconds;
        }
    }

    public static abstract class RenderingMode {
        public @AutoCodec.Ignored RenderingPredicate predicate = ALWAYS_PREDICATE;

        @Environment(EnvType.CLIENT)
        public abstract void render(double capValue, GuiGraphics graphics, int x, int y);

        public abstract int getWidth();
        public abstract int getHeight();
    }

    @AutoCodec.Settings(optionalByDefault = true)
    public static class BarRenderingMode extends RenderingMode {
        public static final Codec<BarRenderingMode> codec = AutoCodec.of(BarRenderingMode.class).codec();

        public int width = 10;
        public int height = 2;
        public @AutoCodec.Mandatory double maxValue;
        public Color filledColor = Color.WHITE;
        public Color emptyColor = Color.GRAY;

        @Environment(EnvType.CLIENT)
        @Override
        public void render(double capValue, GuiGraphics graphics, int x, int y) {
            double fillPercent = capValue/maxValue;
            int filledX = (int) Math.round(x + fillPercent*width);
            graphics.fill(x, y, filledX, y+height, filledColor.argb());
            graphics.fill(filledX+1, y, x + width, y+height, emptyColor.argb());
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
