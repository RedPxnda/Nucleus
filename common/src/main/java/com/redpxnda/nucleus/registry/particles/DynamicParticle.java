package com.redpxnda.nucleus.registry.particles;

import com.mojang.blaze3d.vertex.*;
import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.registry.particles.manager.DynamicParticleManager;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleShape;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public abstract class DynamicParticle extends Particle implements DynamicParticleManager {
    public int expectedLifetime;
    public int lifetimeMarginMin = 0;
    public int lifetimeMarginMax = 0;
    public float red;
    public float green;
    public float blue;
    public float alpha;
    public float scale;
    public float oldScale;
    public ParticleShape.MotionFunction motionFunction = (a, b, c) -> {};

    protected DynamicParticle(ClientLevel clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(clientLevel, x, y, z);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.red = 1;
        this.green = 1;
        this.blue = 1;
        this.alpha = 1;
        this.scale = 1;
        this.lifetime = 100;
    }
    public void applyOptions(DynamicParticleOptions options) {
        lifetime = options.lifetime;
        gravity = options.gravity;
        friction = options.friction;
        scale = options.scale;
        red = options.red;
        green = options.green;
        blue = options.blue;
        alpha = options.alpha;
        hasPhysics = options.physics;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public double getXO() {
        return xo;
    }
    public double getYO() {
        return yo;
    }
    public double getZO() {
        return zo;
    }
    public double getXSpeed() {
        return xd;
    }
    public double getYSpeed() {
        return yd;
    }
    public double getZSpeed() {
        return zd;
    }
    @Override
    public int getAge() {
        return age;
    }
    @Override
    public int _getLifetime() {
        return expectedLifetime;
    }
    @Override
    public void setAge(int age) {
        this.age = age;
    }
    @Override
    public void _setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
    public void updateLifetime() {
        this.lifetime = expectedLifetime + MathUtil.random(lifetimeMarginMin, lifetimeMarginMax);
    }
    @Override
    public float getFriction() {
        return friction;
    }
    @Override
    public float getRed() {
        return red;
    }
    @Override
    public float getGreen() {
        return green;
    }
    @Override
    public float getBlue() {
        return blue;
    }
    @Override
    public float getAlpha() {
        return alpha;
    }
    @Override
    public float getScale() {
        return scale;
    }
    @Override
    public void setFriction(float friction) {
        this.friction = friction;
    }
    @Override
    public void setRed(float r) {
        red = r;
    }
    @Override
    public void setGreen(float g) {
        green = g;
    }
    @Override
    public void setBlue(float b) {
        blue = b;
    }
    @Override
    public void _setAlpha(float a) {
        alpha = a;
    }
    @Override
    public void setAlpha(float a) {
        alpha = a;
    }
    @Override
    public void setScale(float s) {
        scale = s;
    }
    public float getGravity() {
        return gravity;
    }
    @Override
    public void setGravity(float grav) {
        this.gravity = grav;
    }
    @Override
    public boolean hasPhysics() {
        return hasPhysics;
    }
    @Override
    public void setPhysicsEnabled(boolean bl) {
        this.hasPhysics = bl;
    }
    public void setXSpeed(double speed) {
        this.xd = speed;
    }
    public void setYSpeed(double speed) {
        this.yd = speed;
    }
    public void setZSpeed(double speed) {
        this.zd = speed;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setZ(double z) {
        this.z = z;
    }
    public ClientLevel getLevel() {
        return this.level;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(f, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(f, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(f, this.zo, this.z) - vec3.z());
        this.render(vertexConsumer, x, y, z, camera, f);
    }

    public abstract void render(VertexConsumer vc, float x, float y, float z, Camera camera, float partialTick);

    @Override
    public void tick() {
        oldScale = scale;
        Vector3d pos = new Vector3d(x, y, z);
        Vector3d motion = new Vector3d(xd, yd, zd);
        motionFunction.move(pos, motion, this.age/((double) this.expectedLifetime));
        this.setXSpeed(motion.x);
        this.setYSpeed(motion.y);
        this.setZSpeed(motion.z);
        this.setPos(pos.x, pos.y, pos.z);
        super.tick();
    }
}
