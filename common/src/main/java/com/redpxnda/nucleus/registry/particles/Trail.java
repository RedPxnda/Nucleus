package com.redpxnda.nucleus.registry.particles;

public class Trail {
    public float width = 0.2f;
    public int saveInterval = 1;
    public int maxLength = 20;
    public boolean emissive = false;
    public float red = 1;
    public float green = 1;
    public float blue = 1;
    public float alpha = 1;

    public Trail setWidth(float width) {
        this.width = width;
        return this;
    }

    public Trail setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public Trail setEmissive(boolean emissive) {
        this.emissive = emissive;
        return this;
    }

    public Trail setSaveInterval(int interval) {
        this.saveInterval = interval;
        return this;
    }

    public Trail setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        return this;
    }

    public Trail setRed(float red) {
        this.red = red;
        return this;
    }

    public Trail setGreen(float green) {
        this.green = green;
        return this;
    }

    public Trail setBlue(float blue) {
        this.blue = blue;
        return this;
    }

    public Trail setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }
}
