package com.donut.builder.module.builder;

import com.donut.builder.module.movement.HumanRotation;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class GhostPlacer {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final PlaceRateController rate = new PlaceRateController();
    private final HumanRotation rotation = new HumanRotation();

    public void tick() { rotation.tick(); }

    public boolean placeAt(BlockPos pos, BlockState expected) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return false;
        if (!rate.canPlace()) return false;

        Direction face = null;
        BlockPos neighbor = null;
        for (Direction d : Direction.values()) {
            BlockPos n = pos.offset(d);
            if (!mc.world.getBlockState(n).isAir()) {
                face = d.getOpposite();
                neighbor = n;
                break;
            }
        }
        if (face == null) return false;

        int slot = findSlot(expected);
        if (slot < 0) return false;
        mc.player.getInventory().selectedSlot = slot;

        Vec3d center = Vec3d.ofCenter(neighbor).add(
            face.getOffsetX() * 0.5, face.getOffsetY() * 0.5, face.getOffsetZ() * 0.5);
        Vec3d eye = mc.player.getEyePos();
        if (eye.distanceTo(Vec3d.ofCenter(pos)) > 4.4) return false;

        float yaw = (float) Math.toDegrees(Math.atan2(-(center.x - eye.x), center.z - eye.z));
        float pitch = (float) -Math.toDegrees(Math.atan2(center.y - eye.y,
            Math.hypot(center.x - eye.x, center.z - eye.z)));
        rotation.lookAt(yaw, pitch);
        if (!rotation.isIdle()) return false;

        BlockHitResult hit = new BlockHitResult(center, face, neighbor, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
        return true;
    }

    public boolean placeFloating(BlockPos pos, BlockState expected) {
        BlockPos scaffold = pos.offset(Direction.DOWN);
        if (mc.world.getBlockState(scaffold).isAir()) {
            if (!placeAt(scaffold, expected)) return false;
        }
        return placeAt(pos, expected);
    }

    private int findSlot(BlockState state) {
        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty()) continue;
            if (!(s.getItem() instanceof BlockItem bi)) continue;
            if (bi.getBlock().getDefaultState().equals(state)) return i;
        }
        return -1;
    }
}
