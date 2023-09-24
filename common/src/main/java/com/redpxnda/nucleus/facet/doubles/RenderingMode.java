package com.redpxnda.nucleus.facet.doubles;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.codec.AutoCodec;
import com.redpxnda.nucleus.math.InterpolateMode;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.util.Color;
import com.redpxnda.nucleus.util.GuiDrawUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

@Environment(EnvType.CLIENT)
public abstract class RenderingMode {
    public @AutoCodec.Ignored RenderingPredicate predicate = RenderingPredicate.ALWAYS_PREDICATE;
    public @AutoCodec.Ignored int margin = 2;
    public @AutoCodec.Ignored InterpolateMode interpolate = InterpolateMode.NONE;
    public @AutoCodec.Ignored float interpolateTime = 1;
    public @AutoCodec.Ignored boolean adjustInterpolateTarget = true; // true enables behavior to prevent jumps when values change in the middle of an interpolation animation

    public abstract void render(double capValue, DrawContext graphics, int x, int y, float alpha);

    public abstract int getWidth();

    public abstract int getHeight();

    @Environment(EnvType.CLIENT)
    public interface Creator {
        RenderingMode createFrom(JsonElement element);
    }

    @Environment(EnvType.CLIENT)
    @AutoCodec.Settings(optionalByDefault = true)
    public static class Bar extends RenderingMode {
        public static final Codec<Bar> codec = AutoCodec.of(Bar.class).codec();

        public int width = 16;
        public int height = 2;
        public @AutoCodec.Mandatory double maxValue;
        public Color filledColor = Color.WHITE;
        public Color emptyColor = Color.GRAY;

        @Override
        public void render(double capValue, DrawContext graphics, int x, int y, float alpha) {
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
