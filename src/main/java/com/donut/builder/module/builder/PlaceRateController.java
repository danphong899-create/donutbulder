package com.donut.builder.module.builder;

import com.donut.builder.config.ModConfig;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public class PlaceRateController {
    private final Queue<Long> recentPlaces = new ArrayDeque<>();
    private final Random rand = new Random();

    public boolean canPlace() {
        long now = System.currentTimeMillis();
        recentPlaces.removeIf(t -> now - t > 1000);
        if (recentPlaces.size() >= ModConfig.INSTANCE.placeRateLimit) return false;
        if (rand.nextInt(100) < 15) {
            try { Thread.sleep(100 + rand.nextInt(300)); }
            catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
        recentPlaces.add(now);
        return true;
    }
}
