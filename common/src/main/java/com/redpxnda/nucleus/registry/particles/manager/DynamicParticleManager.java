package com.redpxnda.nucleus.registry.particles.manager;

import com.mojang.serialization.Codec;
import com.redpxnda.nucleus.datapack.codec.ValueAssigner;

public interface DynamicParticleManager {
    ValueAssigner.CodecBuilder<DynamicParticleManager> setupBuilder = new ValueAssigner.CodecBuilder<DynamicParticleManager>()
            .add("lifetime", Codec.INT, DynamicParticleManager::_setLifetime, 100)
            .add("friction", Codec.FLOAT, DynamicParticleManager::setFriction, 0.98f)
            .add("gravity", Codec.FLOAT, DynamicParticleManager::setGravity, 0f)
            .add("red", Codec.FLOAT, DynamicParticleManager::setRed, 1f)
            .add("green", Codec.FLOAT, DynamicParticleManager::setGreen, 1f)
            .add("blue", Codec.FLOAT, DynamicParticleManager::setBlue, 1f)
            .add("alpha", Codec.FLOAT, DynamicParticleManager::_setAlpha, 1f)
            .add("scale", Codec.FLOAT, DynamicParticleManager::setScale, 1f)
            .add("physics", Codec.BOOL, DynamicParticleManager::setPhysicsEnabled, true);
    ValueAssigner.CodecBuilder<DynamicParticleManager> tickBuilder = new ValueAssigner.CodecBuilder<DynamicParticleManager>()
            .add("gravity", Codec.FLOAT, (p, f) -> p.setGravity(p.getGravity()+f), 0f)
            .add("red", Codec.FLOAT, (p, f) -> p.setRed(p.getRed()+f), 0f)
            .add("green", Codec.FLOAT, (p, f) -> p.setGreen(p.getGreen()+f), 0f)
            .add("blue", Codec.FLOAT, (p, f) -> p.setBlue(p.getBlue()+f), 0f)
            .add("alpha", Codec.FLOAT, (p, f) -> p._setAlpha(p.getAlpha()+f), 0f)
            .add("scale", Codec.FLOAT, (p, f) -> p.setScale(p.getScale()+f), 0f);
    Codec<ValueAssigner<DynamicParticleManager>> setup = setupBuilder.build();
    Codec<ValueAssigner<DynamicParticleManager>> tick = tickBuilder.build();

    double getX();
    double getY();
    double getZ();
    double getXSpeed();
    double getYSpeed();
    double getZSpeed();
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
    void setX(double x);
    void setY(double y);
    void setZ(double z);
    void setXSpeed(double s);
    void setYSpeed(double s);
    void setZSpeed(double s);
}
