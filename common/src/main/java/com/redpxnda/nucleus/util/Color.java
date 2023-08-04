package com.redpxnda.nucleus.util;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Vector4f;
import org.joml.Vector4i;

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

    public Color(float r, float g, float b, float a) {
        this.x = (int)(r / 255);
        this.y = (int)(g / 255);
        this.z = (int)(b / 255);
        this.w = (int)(a / 255);
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
        this((int) (vec.x*255), (int) (vec.y*255), (int) (vec.z*255), (int) (vec.w*255));
    }

    public static Color fromFloatVecNoMult(Vector4f vec) {
        return new Color((int) vec.x, (int) vec.y, (int) vec.z, (int) vec.w);
    }

    public Color(int argb) {
        x = FastColor.ARGB32.red(argb);
        z = FastColor.ARGB32.blue(argb);
        y = FastColor.ARGB32.green(argb);
        w = FastColor.ARGB32.alpha(argb);
    }

    public Color(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        validateHexString(hex);
        int hexInt = Integer.parseInt(hex, 16);

        x = (hexInt >> 16) & 0xFF;
        y = (hexInt >> 8) & 0xFF;
        z = hexInt & 0xFF;
        w = 255;
    }

    public Color() {}

    public static void validateHexString(String hex) {
        if (!hex.replaceFirst("^[0-9A-Fa-f]{6}$", "").isEmpty())
            throw new RuntimeException("Invalid rgb hex! Don't know how to use '" + hex + "'! Only use 0-9, a-f(case insensitive), and make sure there are exactly 6 characters. Eg: '9Ad6F0'");
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
        return FastColor.ARGB32.color(w, x, y, z);
    }
    public int abgr() {
        return FastColor.ARGB32.color(w, z, y, x);
    }

    public String hex() {
        return String.format("%02x%02x%02x", x, y, z);
    }
    public String hexWithPrefix() {
        return String.format("#%02x%02x%02x", x, y, z);
    }
    public int hexInt() {
        int rgb = x;
        rgb = (rgb << 8) + y;
        rgb = (rgb << 8) + z;
        return rgb;
    }

    public void lerp(float delta, Color other) {
        x = Mth.lerpInt(delta, x, other.x);
        y = Mth.lerpInt(delta, y, other.y);
        z = Mth.lerpInt(delta, z, other.z);
        w = Mth.lerpInt(delta, w, other.w);
    }
    public void lerp(float delta, Color other, Color dest) {
        dest.x = Mth.lerpInt(delta, x, other.x);
        dest.y = Mth.lerpInt(delta, y, other.y);
        dest.z = Mth.lerpInt(delta, z, other.z);
        dest.w = Mth.lerpInt(delta, w, other.w);
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
