package com.donut.builder.module.builder;

import com.donut.builder.config.ModConfig;
import com.donut.builder.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.List;

public class AutoBuilder extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static int placed = 0;
    public static int total = 0;

    private final SchematicLoader schematic = new SchematicLoader();
    private final LayerBuilder layers = new LayerBuilder();
    private final GhostPlacer placer = new GhostPlacer();

    private BlockPos origin = null;
    private boolean loaded = false;

    public AutoBuilder() { super("autobuilder"); }

    public void load(File litematic, BlockPos origin) throws Exception {
        schematic.loadLitematic(litematic);
        List<BlockPos> sorted = schematic.sortedByLayer(origin);
        layers.setPositions(sorted);
        this.origin = origin;
        this.loaded = true;
        placed = 0;
        total = sorted.size();
    }

    public void rotate(int quarters) {
        schematic.rotateY(quarters);
        if (origin != null) {
            List<BlockPos> sorted = schematic.sortedByLayer(origin);
            layers.setPositions(sorted);
            total = sorted.size();
            placed = 0;
        }
    }

    @Override public void onEnable() {}
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        if (!enabled || !loaded || mc.player == null || mc.world == null) return;
        placer.tick();
        BlockPos next = layers.next();
        if (next == null) { toggle(); return; }
        if (!mc.world.getBlockState(next).isAir()) return;

        BlockState expected = schematic.getAt(
            next.getX() - origin.getX(),
            next.getY() - origin.getY(),
            next.getZ() - origin.getZ());

        boolean ok;
        if (needsScaffold(next)) ok = placer.placeFloating(next, expected);
        else ok = placer.placeAt(next, expected);
        if (ok) placed++;

        try { Thread.sleep(ModConfig.INSTANCE.layerDelayMs / 4); }
        catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }

    private boolean needsScaffold(BlockPos pos) {
        for (var d : net.minecraft.util.math.Direction.values()) {
            if (!mc.world.getBlockState(pos.offset(d)).isAir()) return false;
        }
        return true;
    }
}
