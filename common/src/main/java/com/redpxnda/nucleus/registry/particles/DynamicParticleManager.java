package com.redpxnda.nucleus.registry.particles;

public interface DynamicParticleManager {
    void _setLifetime(int life); // I have to put underscores to differentiate with the vanilla methods- I hate obfuscation.
    void setAge(int life);
    void setFriction(float fric);
    void setGravity(float fric);
    void setRed(float r);
    void setGreen(float g);
    void setBlue(float b);
    void _setAlpha(float a);
    void setScale(float scale);
    void setPhysicsEnabled(boolean physics);
    int _getLifetime();
    int getAge();
    float getFriction();
    float getGravity();
    float getRed();
    float getGreen();
    float getBlue();
    float getAlpha();
    float getScale();
    boolean hasPhysics();
}
