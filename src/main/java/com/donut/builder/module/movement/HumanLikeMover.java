package com.donut.builder.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class HumanLikeMover {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Random rand = new Random();

    private Vec3d target;
    private int tickJitter;
    private float yawJitter;

    public void setTarget(Vec3d pos) { this.target = pos; }
    public boolean hasTarget() { return target != null; }
    public void clear() { target = null; }

    public void tick() {
        ClientPlayerEntity p = mc.player;
        if (p == null || target == null) return;
        Vec3d delta = target.subtract(p.getPos());
        if (delta.lengthSquared() < 0.5) { target = null; return; }

        float desiredYaw = (float) Math.toDegrees(Math.atan2(-delta.x, delta.z));
        if (yawJitter == 0 || rand.nextInt(20) == 0)
            yawJitter = (rand.nextFloat() - 0.5f) * 6f;
        float yaw = desiredYaw + yawJitter;
        float pitch = (float) -Math.toDegrees(Math.atan2(delta.y,
            Math.sqrt(delta.x * delta.x + delta.z * delta.z)));
        pitch += (rand.nextFloat() - 0.5f) * 4f;
        p.setYaw(yaw);
        p.setPitch(pitch);

        if (tickJitter > 0) { tickJitter--; return; }
        if (rand.nextInt(40) == 0) { tickJitter = 3 + rand.nextInt(8); return; }

        p.input.movementForward = 1.0f;
        p.input.movementSideways = 0f;
        p.setSprinting(rand.nextInt(10) > 2);
        if (p.isOnGround() && delta.y > 0.5 && rand.nextInt(5) == 0) p.jump();
    }
}
