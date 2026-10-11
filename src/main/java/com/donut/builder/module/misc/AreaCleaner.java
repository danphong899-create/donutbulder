package com.donut.builder.module.misc;

import com.donut.builder.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.Queue;

public class AreaCleaner extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static int radius = 16;
    private final Queue<BlockPos> queue = new ArrayDeque<>();
    private int cooldown = 0;

    public AreaCleaner() { super("areacleaner"); }

    @Override public void onEnable() { scan(); }
    @Override public void onDisable() { queue.clear(); }

    private void scan() {
        if (mc.player == null) return;
        BlockPos c = mc.player.getBlockPos();
        for (int dx = -radius; dx <= radius; dx++)
            for (int dy = -8; dy <= 8; dy++)
                for (int dz = -radius; dz <= radius; dz++)
                    queue.add(c.add(dx, dy, dz));
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (cooldown-- > 0) return;
        cooldown = 4;
        while (!queue.isEmpty()) {
            BlockPos p = queue.poll();
            if (mc.world.getBlockState(p).isAir()) continue;
            if (p.equals(mc.player.getBlockPos())) continue;
            mc.interactionManager.updateBlockBreakingProgress(p, Direction.UP);
            mc.player.swingHand(Hand.MAIN_HAND);
            return;
        }
    }
}
