package com.donut.builder.module.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.List;

public class GhostBlockRenderer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void render(MatrixStack matrices, List<BlockPos> positions, int color) {
        if (positions.isEmpty()) return;
        Camera cam = mc.gameRenderer.getCamera();
        Vec3d camPos = cam.getPos();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        Matrix4f mat = matrices.peek().getPositionMatrix();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        for (BlockPos pos : positions) {
            Box box = new Box(pos).offset(-camPos.x, -camPos.y, -camPos.z).contract(0.002);
            pushBox(buf, mat, box, r, g, b, a);
        }
        BufferRenderer.drawWithGlobalProgram(buf.end());
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void pushBox(BufferBuilder b, Matrix4f m, Box box, float r, float g, float bl, float a) {
        quad(b, m, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, box.minX, box.maxY, box.minZ, r, g, bl, a);
        quad(b, m, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ, r, g, bl, a);
        quad(b, m, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, box.minX, box.maxY, box.maxZ, box.minX, box.minY, box.maxZ, r, g, bl, a);
        quad(b, m, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, box.maxX, box.maxY, box.minZ, box.maxX, box.minY, box.minZ, r, g, bl, a);
        quad(b, m, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ, box.maxX, box.minY, box.minZ, r, g, bl, a);
        quad(b, m, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, bl, a);
    }

    private static void quad(BufferBuilder b, Matrix4f m,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4,
                             float r, float g, float bl, float a) {
        b.vertex(m, x1, y1, z1).color(r, g, bl, a);
        b.vertex(m, x2, y2, z2).color(r, g, bl, a);
        b.vertex(m, x3, y3, z3).color(r, g, bl, a);
        b.vertex(m, x4, y4, z4).color(r, g, bl, a);
    }
}
