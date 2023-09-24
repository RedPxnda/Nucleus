package com.redpxnda.nucleus.facet.doubles;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.redpxnda.nucleus.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public interface RenderingPredicate {
    RenderingPredicate ALWAYS_PREDICATE = (v, t) -> true;
    RenderingPredicate NEVER_PREDICATE = (v, t) -> false;

    /**
     * @param val  value of the double capability in question
     * @param time time of last modification of the double capability
     * @return whether the double capability can render or not
     */
    boolean canRender(double val, long time);

    default float getAlpha(long time) {
        return 1;
    }

    @Environment(EnvType.CLIENT)
    interface Creator {
        RenderingPredicate createFrom(JsonElement element);
    }

    @Environment(EnvType.CLIENT)
    class RecentModificationRP implements RenderingPredicate {
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
            long currentTime = Util.getMeasuringTimeMs();
            double dif = (currentTime - time) / 1000d;
            return dif < seconds; // didnt inline this stuff for testing purposes
        }

        @Override
        public float getAlpha(long time) {
            long currentTime = Util.getMeasuringTimeMs();
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
}
