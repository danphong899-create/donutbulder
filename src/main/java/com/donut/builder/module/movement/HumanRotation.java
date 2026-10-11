package com.donut.builder.module.movement;

import net.minecraft.client.MinecraftClient;

public class HumanRotation {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private float yaw, pitch;
    private float targetYaw, targetPitch;
    private float bezierT = 1f;
    private boolean active = false;
    private static final float BEZIER_DURATION_TICKS = 11f;

    public void lookAt(float yawDeg, float pitchDeg) {
        if (!active && mc.player != null) {
            this.yaw = mc.player.getYaw();
            this.pitch = mc.player.getPitch();
        }
        this.targetYaw = yawDeg;
        this.targetPitch = Math.max(-90f, Math.min(90f, pitchDeg));
        this.bezierT = 0f;
        this.active = true;
    }

    public void tick() {
        if (!active || mc.player == null) return;
        bezierT = Math.min(1f, bezierT + 1f / BEZIER_DURATION_TICKS);
        float t = bezierT * bezierT * (3 - 2 * bezierT);
        float rawYaw = lerp(yaw, targetYaw, t);
        float rawPitch = lerp(pitch, targetPitch, t);
        float gcd = computeGCD();
        yaw = Math.round(rawYaw / gcd) * gcd;
        pitch = Math.round(rawPitch / gcd) * gcd;
        pitch = Math.max(-90f, Math.min(90f, pitch));
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
        if (bezierT >= 1f) active = false;
    }

    public boolean isIdle() { return !active; }

    private float computeGCD() {
        float sens = mc.options.getMouseSensitivity().getValue().floatValue();
        float f = sens * 0.6f + 0.2f;
        return f * f * f * 1.2f;
    }

    private float lerp(float a, float b, float t) { return a + (b - a) * t; }
}
