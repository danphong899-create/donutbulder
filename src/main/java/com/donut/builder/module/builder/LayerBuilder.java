package com.donut.builder.module.builder;

import net.minecraft.util.math.BlockPos;
import java.util.List;

public class LayerBuilder {
    public int currentLayer = 0;
    public int totalLayers = 0;
    private List<BlockPos> sorted;
    private int cursor = 0;
    private int currentY = Integer.MIN_VALUE;

    public void setPositions(List<BlockPos> sorted) {
        this.sorted = sorted;
        this.cursor = 0;
        this.currentLayer = 0;
        this.currentY = sorted.isEmpty() ? 0 : sorted.get(0).getY();
        this.totalLayers = sorted.isEmpty() ? 0 :
            (sorted.get(sorted.size() - 1).getY() - sorted.get(0).getY() + 1);
    }

    public BlockPos next() {
        if (sorted == null || cursor >= sorted.size()) return null;
        BlockPos p = sorted.get(cursor);
        if (p.getY() != currentY) { currentY = p.getY(); currentLayer++; }
        cursor++;
        return p;
    }

    public boolean done() { return sorted == null || cursor >= sorted.size(); }
    public int progress() { return sorted == null ? 0 : cursor; }
    public int total() { return sorted == null ? 0 : sorted.size(); }
}
