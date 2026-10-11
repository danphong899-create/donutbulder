package com.donut.builder.module.inventory;

import com.donut.builder.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class AutoSeller extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public String[] sellList = new String[]{};

    public AutoSeller() { super("autoseller"); }

    @Override public void onEnable() {}
    @Override public void onDisable() {}

    @Override
    public void onTick() {
        if (mc.player == null || mc.currentScreen == null) return;
        var handler = mc.player.currentScreenHandler;
        for (int i = 0; i < handler.slots.size(); i++) {
            ItemStack s = handler.slots.get(i).getStack();
            if (s.isEmpty()) continue;
            for (String id : sellList) {
                if (s.getItem().toString().contains(id)) {
                    mc.interactionManager.clickSlot(handler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                }
            }
        }
    }
}
