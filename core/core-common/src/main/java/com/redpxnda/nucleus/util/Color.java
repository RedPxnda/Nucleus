package com.redpxnda.nucleus.util;

import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import org.joml.Vector4i;

import java.util.function.Function;

/**
 * Simple color class.
 * x = red
 * y = green
 * z = blue
 * w = alpha
 * <p></p>
 * Why use xyzw? Cuz I'm lazy and extending Vector4i makes it easy
 */
public class Color extends Vector4i {
    public static final Color WHITE = new Color(255, 255, 255, 255);
    public static final Color BLACK = new Color(0, 0, 0, 255);
    public static final Color GRAY = new Color(123, 123, 123, 255);
    public static final Color RED = new Color(255, 0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0, 255);
    public static final Color GREEN = new Color(0, 255, 0, 255);
    public static final Color AQUA = new Color(0, 255, 255, 255);
    public static final Color BLUE = new Color(0, 0, 255, 255);
    public static final Color MAGENTA = new Color(255, 0, 255, 255);
    public static final Color[] RAINBOW = {RED, YELLOW, GREEN, AQUA, BLUE, MAGENTA};

    public static final Color TEXT_DARK_GRAY = fromRgbInt(Formatting.DARK_GRAY.getColorValue());
    public static final Color TEXT_GRAY = fromRgbInt(Formatting.GRAY.getColorValue());

    protected Integer cachedArgb = null;
    protected String cachedHex = null;

    public static Color fromRgbInt(int rgb) {
        int r = (rgb & 0xFF0000) >> 16;
        int g = (rgb & 0xFF00) >> 8;
        int b = (rgb & 0xFF);
        return new Color(r, g, b, 255);
    }

    public Color(float r, float g, float b, float a) {
        this.x = Math.round(r * 255);
        this.y = Math.round(g * 255);
        this.z = Math.round(b * 255);
        this.w = Math.round(a * 255);
    }

    public Color(int r, int g, int b, int a) {
        this.x = r;
        this.y = g;
        this.z = b;
        this.w = a;
    }

    public Color(Vector4i vec) {
        this(vec.x, vec.y, vec.z, vec.w);
    }

    /**
     * Creates a color from a float vector- keep in mind this assumes 1f = 255
     */
    public Color(Vector4f vec) {
        this(Math.round(vec.x*255), Math.round(vec.y*255), Math.round(vec.z*255), Math.round(vec.w*255));
    }

    public static Color fromFloatVecNoMult(Vector4f vec) {
        return new Color(Math.round(vec.x), Math.round(vec.y), Math.round(vec.z), Math.round(vec.w));
    }

    public Color(int argb) {
        x = ColorHelper.Argb.getRed(argb);
        z = ColorHelper.Argb.getBlue(argb);
        y = ColorHelper.Argb.getGreen(argb);
        w = ColorHelper.Argb.getAlpha(argb);
        cachedArgb = argb;
    }

    public Color(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        validateHexString(hex);

        String alpha = "FF";
        if (hex.length() == 8) {
            cachedHex = hex;
            alpha = hex.substring(6, 8);
            hex = hex.substring(0, 6);
        } else cachedHex = hex + alpha;
        int hexInt = Integer.parseInt(hex, 16);

        x = (hexInt >> 16) & 0xFF;
        y = (hexInt >> 8) & 0xFF;
        z = hexInt & 0xFF;
        w = Integer.parseInt(alpha, 16);
    }

    public Color() {}

    public static void validateHexString(String hex) {
        if (!isValidHexString(hex))
            throw new RuntimeException("Invalid rgb hex! Don't know how to use '" + hex + "'! Only use 0-9, a-f(case insensitive), and make sure there are exactly 6 or 8 characters.(8 if you use alpha, extra 2 at end)  Eg: '9Ad6F0'");
    }

    public static boolean isValidHexString(String hex) {
        return !hex.isEmpty() && (hex.replaceFirst("^[0-9A-Fa-f]{6}$", "").isEmpty() || hex.replaceFirst("^[0-9A-Fa-f]{8}$", "").isEmpty());
    }

    public void resetCached() {
        cachedArgb = null;
        cachedHex = null;
    }

    public void map(Function<Integer, Integer> mapFunc) {
        x = mapFunc.apply(x);
        y = mapFunc.apply(y);
        z = mapFunc.apply(z);
        w = mapFunc.apply(w);
    }
    public void mapR(Function<Integer, Integer> mapFunc) {
        x = mapFunc.apply(x);
        resetCached();
    }
    public void mapG(Function<Integer, Integer> mapFunc) {
        y = mapFunc.apply(y);
        resetCached();
    }
    public void mapB(Function<Integer, Integer> mapFunc) {
        z = mapFunc.apply(z);
        resetCached();
    }
    public void mapA(Function<Integer, Integer> mapFunc) {
        w = mapFunc.apply(w);
        resetCached();
    }

    public void setRed(int red) {
        x = red;
        resetCached();
    }
    public void setGreen(int green) {
        y = green;
        resetCached();
    }
    public void setBlue(int blue) {
        z = blue;
        resetCached();
    }
    public void setAlpha(int alpha) {
        w = alpha;
        resetCached();
    }

    public void setRed(float red) {
        x = Math.round(red*255);
        resetCached();
    }
    public void setGreen(float green) {
        y = Math.round(green*255);
        resetCached();
    }
    public void setBlue(float blue) {
        z = Math.round(blue*255);
        resetCached();
    }
    public void setAlpha(float alpha) {
        w = Math.round(alpha*255);
        resetCached();
    }

    public Color withRed(int red) {
        Color result = copy();
        result.setRed(red);
        return result;
    }
    public Color withGreen(int green) {
        Color result = copy();
        result.setGreen(green);
        return result;
    }
    public Color withBlue(int blue) {
        Color result = copy();
        result.setBlue(blue);
        return result;
    }
    public Color withAlpha(int alpha) {
        Color result = copy();
        result.setAlpha(alpha);
        return result;
    }

    public Color withRed(float red) {
        Color result = copy();
        result.setRed(red);
        return result;
    }
    public Color withGreen(float green) {
        Color result = copy();
        result.setGreen(green);
        return result;
    }
    public Color withBlue(float blue) {
        Color result = copy();
        result.setBlue(blue);
        return result;
    }
    public Color withAlpha(float alpha) {
        Color result = copy();
        result.setAlpha(alpha);
        return result;
    }

    public Color copy() {
        return new Color(x, y, z, w);
    }

    public int red() {
        return x;
    }
    public int green() {
        return y;
    }
    public int blue() {
        return z;
    }
    public int alpha() {
        return w;
    }
    public int r() {
        return x;
    }
    public int g() {
        return y;
    }
    public int b() {
        return z;
    }
    public int a() {
        return w;
    }
    public float redAsFloat() {
        return x /255f;
    }
    public float greenAsFloat() {
        return y /255f;
    }
    public float blueAsFloat() {
        return z /255f;
    }
    public float alphaAsFloat() {
        return w /255f;
    }

    public int argb() {
        if (cachedArgb == null) cachedArgb = ColorHelper.Argb.getArgb(w, x, y, z);
        return cachedArgb;
    }
    public int abgr() {
        return ColorHelper.Abgr.getAbgr(w, z, y, x);
    }

    public String hexNoAlpha() {
        return String.format("%02x%02x%02x", x, y, z);
    }
    public String hex() {
        if (cachedHex == null) cachedHex = String.format("%02x%02x%02x%02x", x, y, z, w);
        return cachedHex;
    }
    public String hexWithPrefix() {
        return "#" + hex();
    }
    public int hexInt() {
        int rgb = x;
        rgb = (rgb << 8) + y;
        rgb = (rgb << 8) + z;
        return rgb;
    }

    public void lerp(float delta, Color other) {
        x = MathHelper.lerp(delta, x, other.x);
        y = MathHelper.lerp(delta, y, other.y);
        z = MathHelper.lerp(delta, z, other.z);
        w = MathHelper.lerp(delta, w, other.w);
        resetCached();
    }
    public void lerp(float delta, Color other, Color dest) {
        dest.x = MathHelper.lerp(delta, x, other.x);
        dest.y = MathHelper.lerp(delta, y, other.y);
        dest.z = MathHelper.lerp(delta, z, other.z);
        dest.w = MathHelper.lerp(delta, w, other.w);
        dest.resetCached();
    }

    @Override
    public String toString() {
        return "Color{" +
                "r=" + x +
                ", g=" + y +
                ", b=" + z +
                ", a=" + w +
                '}';
    }

    public Vector4f toFloatVec() {
        return new Vector4f(x/255f, y/255f, z/255f, w/255f);
    }
    public Vector4f toFloatVecNoDiv() {
        return new Vector4f(x, y, z, w);
    }
}
