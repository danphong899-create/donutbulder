package com.donut.builder.module.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class ShulkerExtractor {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public void openShulkerInInventory(int slotIndex) {
        if (mc.player == null || mc.interactionManager == null) return;
        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slotIndex, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, slotIndex, 0, SlotActionType.PICKUP, mc.player);
    }

    public int countMaterial(String itemIdSubstring) {
        if (mc.player == null) return 0;
        int n = 0;
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty()) continue;
            if (s.getItem().toString().contains(itemIdSubstring)) n += s.getCount();
        }
        return n;
    }

    public void extractMaterial(String itemIdSubstring, int amount) {
        if (mc.player == null || mc.currentScreen == null) return;
        var handler = mc.player.currentScreenHandler;
        for (int i = 0; i < handler.slots.size(); i++) {
            ItemStack s = handler.slots.get(i).getStack();
            if (s.isEmpty()) continue;
            if (s.getItem().toString().contains(itemIdSubstring)) {
                mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                if (--amount <= 0) break;
            }
        }
    }
}
