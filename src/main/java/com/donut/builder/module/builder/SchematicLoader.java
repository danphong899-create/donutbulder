package com.donut.builder.module.builder;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchematicLoader {
    public final Map<Long, BlockState> blocks = new HashMap<>();
    public int sizeX, sizeY, sizeZ;

    public void loadLitematic(File file) throws Exception {
        NbtCompound root = NbtIo.readCompressed(file.toPath(), net.minecraft.nbt.NbtSizeTracker.ofUnlimitedBytes());
        NbtCompound regions = root.getCompound("Regions");
        for (String key : regions.getKeys()) {
            NbtCompound region = regions.getCompound(key);
            NbtCompound size = region.getCompound("Size");
            sizeX = Math.abs(size.getInt("x"));
            sizeY = Math.abs(size.getInt("y"));
            sizeZ = Math.abs(size.getInt("z"));

            NbtList palette = region.getList("BlockStatePalette", NbtElement.COMPOUND_TYPE);
            BlockState[] pal = new BlockState[palette.size()];
            for (int i = 0; i < palette.size(); i++) {
                NbtCompound entry = palette.getCompound(i);
                String name = entry.getString("Name");
                BlockState st = Registries.BLOCK.get(Identifier.tryParse(name)).getDefaultState();
                pal[i] = st;
            }

            NbtList bsList = region.getList("BlockStates", NbtElement.LONG_TYPE);
            long[] data = new long[bsList.size()];
            for (int i = 0; i < bsList.size(); i++) data[i] = bsList.getLong(i);

            int bits = Math.max(2, 32 - Integer.numberOfLeadingZeros(pal.length - 1));
            long mask = (1L << bits) - 1L;
            int total = sizeX * sizeY * sizeZ;
            int perLong = 64 / bits;

            for (int idx = 0; idx < total; idx++) {
                int li = idx / perLong;
                int off = (idx % perLong) * bits;
                if (li >= data.length) break;
                int pi = (int) ((data[li] >>> off) & mask);
                if (pi >= pal.length) continue;
                BlockState st = pal[pi];
                if (st.isAir()) continue;
                int x = idx % sizeX;
                int y = (idx / sizeX) % sizeY;
                int z = idx / (sizeX * sizeY);
                blocks.put(pack(x, y, z), st);
            }
        }
    }

    public List<BlockPos> sortedByLayer(BlockPos origin) {
        List<BlockPos> out = new ArrayList<>(blocks.size());
        for (long key : blocks.keySet()) {
            int x = (int) (key & 0x3FF);
            int y = (int) ((key >> 10) & 0xFFF);
            int z = (int) ((key >> 22) & 0x3FF);
            out.add(origin.add(x, y, z));
        }
        out.sort((a, b) -> {
            int c = Integer.compare(a.getY(), b.getY());
            if (c != 0) return c;
            c = Integer.compare(a.getZ(), b.getZ());
            if (c != 0) return c;
            return Integer.compare(a.getX(), b.getX());
        });
        return out;
    }

    public BlockState getAt(int x, int y, int z) {
        return blocks.getOrDefault(pack(x, y, z), Blocks.AIR.getDefaultState());
    }

    public void rotateY(int quarterTurns) {
        int turns = ((quarterTurns % 4) + 4) % 4;
        if (turns == 0) return;
        Map<Long, BlockState> next = new HashMap<>();
        int nx = sizeX, ny = sizeY, nz = sizeZ;
        for (var e : blocks.entrySet()) {
            long k = e.getKey();
            int x = (int) (k & 0x3FF);
            int y = (int) ((k >> 10) & 0xFFF);
            int z = (int) ((k >> 22) & 0x3FF);
            int rx = x, rz = z;
            for (int i = 0; i < turns; i++) {
                int tx = rx;
                rx = nz - 1 - rz;
                rz = tx;
                int t = nx; nx = nz; nz = t;
            }
            BlockState st = e.getValue();
            for (int i = 0; i < turns; i++) st = st.rotate(net.minecraft.block.enums.BlockRotation.CLOCKWISE_90);
            next.put(pack(rx, y, rz), st);
        }
        sizeX = nx; sizeZ = nz;
        blocks.clear();
        blocks.putAll(next);
    }

    private static long pack(int x, int y, int z) {
        return (x & 0x3FFL) | ((long)(y & 0xFFF) << 10) | ((long)(z & 0x3FF) << 22);
    }
}
