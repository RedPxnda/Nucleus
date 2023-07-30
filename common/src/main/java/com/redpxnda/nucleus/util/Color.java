package com.redpxnda.nucleus.util;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class Color {
    public int r;
    public int g;
    public int b;
    public int a;

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int argb) {
        r = FastColor.ARGB32.red(argb);
        b = FastColor.ARGB32.blue(argb);
        g = FastColor.ARGB32.green(argb);
        a = FastColor.ARGB32.alpha(argb);
    }

    public Color(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        validateHexString(hex);
        int hexInt = Integer.parseInt(hex, 16);

        r = (hexInt >> 16) & 0xFF;
        g = (hexInt >> 8) & 0xFF;
        b = hexInt & 0xFF;
        a = 255;
    }

    public static void validateHexString(String hex) {
        if (!hex.replaceFirst("^[0-9A-Fa-f]{6}$", "").isEmpty())
            throw new RuntimeException("Invalid rgb hex! Don't know how to use '" + hex + "'! Only use 0-9, a-f(case insensitive), and make sure there are exactly 6 characters. Eg: '9Ad6F0'");
    }

    public float redAsFloat() {
        return r/255f;
    }
    public float greenAsFloat() {
        return g/255f;
    }
    public float blueAsFloat() {
        return b/255f;
    }
    public float alphaAsFloat() {
        return a/255f;
    }

    public int argb() {
        return FastColor.ARGB32.color(a, r, g, b);
    }
    public int abgr() {
        return FastColor.ARGB32.color(a, b, g, r);
    }

    public String hex() {
        return String.format("%02x%02x%02x", r, g, b);
    }
    public String hexWithPrefix() {
        return String.format("#%02x%02x%02x", r, g, b);
    }
    public int hexInt() {
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
    }

    public void lerp(float delta, Color other) {
        r = Mth.lerpInt(delta, r, other.r);
        g = Mth.lerpInt(delta, g, other.g);
        b = Mth.lerpInt(delta, b, other.b);
        a = Mth.lerpInt(delta, a, other.a);
    }
    public void lerp(float delta, Color other, Color dest) {
        dest.r = Mth.lerpInt(delta, r, other.r);
        dest.g = Mth.lerpInt(delta, g, other.g);
        dest.b = Mth.lerpInt(delta, b, other.b);
        dest.a = Mth.lerpInt(delta, a, other.a);
    }

    @Override
    public String toString() {
        return "Color{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                ", a=" + a +
                '}';
    }
}
