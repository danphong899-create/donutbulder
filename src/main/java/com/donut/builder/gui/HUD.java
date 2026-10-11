package com.donut.builder.gui;

import com.donut.builder.module.Module;
import com.donut.builder.module.ModuleManager;
import com.donut.builder.module.builder.AutoBuilder;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class HUD {
    public static void register() {
        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;
            int x = 5, y = 5;
            ctx.drawText(mc.textRenderer, "Donut Builder", x, y, 0xFF00FF00, true);
            y += 12;
            Module ab = ModuleManager.get("autobuilder");
            if (ab != null && ab.enabled) {
                ctx.drawText(mc.textRenderer, "AutoBuilder: " + AutoBuilder.placed + "/" + AutoBuilder.total, x, y, 0xFFFFFF, true);
                y += 12;
            }
            for (Module m : ModuleManager.all()) {
                if (!m.enabled) continue;
                ctx.drawText(mc.textRenderer, m.name + ": ON", x, y, 0xFFAAFFAA, true);
                y += 12;
            }
        });
    }
}
