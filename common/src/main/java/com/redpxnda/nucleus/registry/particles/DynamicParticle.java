package com.redpxnda.nucleus.registry.particles;

import com.redpxnda.nucleus.math.MathUtil;
import com.redpxnda.nucleus.registry.particles.manager.DynamicParticleManager;
import com.redpxnda.nucleus.registry.particles.morphing.ParticleShape;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

    protected DynamicParticle(ClientWorld clientLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(clientLevel, x, y, z);
        this.velocityX = xSpeed;
        this.velocityY = ySpeed;
        this.velocityZ = zSpeed;
        this.red = 1;
        this.green = 1;
        this.blue = 1;
        this.alpha = 1;
        this.scale = 1;
        this.maxAge = 100;
    }
    public void applyOptions(DynamicParticleOptions options) {
        maxAge = options.lifetime;
        gravityStrength = options.gravity;
        velocityMultiplier = options.friction;
        scale = options.scale;
        red = options.red;
        green = options.green;
        blue = options.blue;
        alpha = options.alpha;
        collidesWithWorld = options.physics;
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
        return prevPosX;
    }
    public double getYO() {
        return prevPosY;
    }
    public double getZO() {
        return prevPosZ;
    }
    public double getXSpeed() {
        return velocityX;
    }
    public double getYSpeed() {
        return velocityY;
    }
    public double getZSpeed() {
        return velocityZ;
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
        this.maxAge = lifetime;
    }
    public void updateLifetime() {
        this.maxAge = expectedLifetime + MathUtil.random(lifetimeMarginMin, lifetimeMarginMax);
    }
    @Override
    public float getFriction() {
        return velocityMultiplier;
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
        this.velocityMultiplier = friction;
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
        return gravityStrength;
    }
    @Override
    public void setGravity(float grav) {
        this.gravityStrength = grav;
    }
    @Override
    public boolean hasPhysics() {
        return collidesWithWorld;
    }
    @Override
    public void setPhysicsEnabled(boolean bl) {
        this.collidesWithWorld = bl;
    }
    public void setXSpeed(double speed) {
        this.velocityX = speed;
    }
    public void setYSpeed(double speed) {
        this.velocityY = speed;
    }
    public void setZSpeed(double speed) {
        this.velocityZ = speed;
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
    public ClientWorld getLevel() {
        return this.world;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f) {
        Vec3d vec3 = camera.getPos();
        float x = (float)(MathHelper.lerp(f, this.prevPosX, this.x) - vec3.getX());
        float y = (float)(MathHelper.lerp(f, this.prevPosY, this.y) - vec3.getY());
        float z = (float)(MathHelper.lerp(f, this.prevPosZ, this.z) - vec3.getZ());
        this.render(vertexConsumer, x, y, z, camera, f);
    }

    public abstract void render(VertexConsumer vc, float x, float y, float z, Camera camera, float partialTick);

    @Override
    public void tick() {
        oldScale = scale;
        Vector3d pos = new Vector3d(x, y, z);
        Vector3d motion = new Vector3d(velocityX, velocityY, velocityZ);
        motionFunction.move(pos, motion, this.age/((double) this.expectedLifetime));
        this.setXSpeed(motion.x);
        this.setYSpeed(motion.y);
        this.setZSpeed(motion.z);
        this.setPos(pos.x, pos.y, pos.z);
        super.tick();
    }
}
