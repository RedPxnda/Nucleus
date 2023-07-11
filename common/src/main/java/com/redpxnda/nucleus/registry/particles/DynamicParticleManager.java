package com.redpxnda.nucleus.registry.particles;

public interface DynamicParticleManager {
    void setLifetime(int life);
    void setAge(int life);
    void setFriction(float fric);
    void setRed(float r);
    void setGreen(float g);
    void setBlue(float b);
    void setAlpha(float a);
    void setScale(float scale);
    void setPhysicsEnabled(boolean physics);
    int getLifetime();
    int getAge();
    float getFriction();
    float getRed();
    float getGreen();
    float getBlue();
    float getAlpha();
    float getScale();
    boolean hasPhysics();
}
