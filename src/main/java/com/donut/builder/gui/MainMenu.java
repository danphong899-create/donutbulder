package com.donut.builder.gui;

import com.donut.builder.module.Module;
import com.donut.builder.module.ModuleManager;
import com.donut.builder.module.builder.AutoBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends Screen {
    private static final Path SCHEM_DIR = FabricLoader.getInstance().getGameDir().resolve("schematics");
    private final List<File> schematics = new ArrayList<>();
    private int scroll = 0;

    public MainMenu() { super(Text.literal("Donut Builder")); }

    @Override
    protected void init() {
        schematics.clear();
        File dir = SCHEM_DIR.toFile();
        if (!dir.exists()) dir.mkdirs();
        File[] files = dir.listFiles((d, n) -> n.endsWith(".litematic"));
        if (files != null) for (File f : files) schematics.add(f);

        int cx = width / 2;
        int y = 30;
        addDrawableChild(ButtonWidget.builder(Text.literal("Materials"),
            b -> client.setScreen(new MaterialsPanel()))
            .dimensions(cx - 100, y, 200, 20).build());
        y += 26;

        for (Module m : ModuleManager.all()) {
            final Module mm = m;
            addDrawableChild(ButtonWidget.builder(
                Text.literal((m.enabled ? "[ON] " : "[OFF] ") + m.name),
                b -> { mm.toggle(); clearAndInit(); }
            ).dimensions(cx - 100, y, 200, 20).build());
            y += 24;
        }

        y += 10;
        addDrawableChild(ButtonWidget.builder(Text.literal("Load Schematic"), b -> {
            if (schematics.isEmpty()) return;
            try {
                AutoBuilder ab = (AutoBuilder) ModuleManager.get("autobuilder");
                File f = schematics.get(Math.floorMod(scroll, schematics.size()));
                ab.load(f, client.player.getBlockPos());
            } catch (Exception ignored) {}
        }).dimensions(cx - 100, y, 200, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(Text.literal("< Prev"), b -> { scroll--; clearAndInit(); })
            .dimensions(cx - 100, y, 96, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Next >"), b -> { scroll++; clearAndInit(); })
            .dimensions(cx + 4, y, 96, 20).build());
        y += 24;

        addDrawableChild(ButtonWidget.builder(Text.literal("Rot 90"), b -> {
            AutoBuilder ab = (AutoBuilder) ModuleManager.get("autobuilder");
            if (ab != null) ab.rotate(1);
        }).dimensions(cx - 100, y, 96, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Rot -90"), b -> {
            AutoBuilder ab = (AutoBuilder) ModuleManager.get("autobuilder");
            if (ab != null) ab.rotate(-1);
        }).dimensions(cx + 4, y, 96, 20).build());
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext ctx, int mx, int my, float d) {
        renderBackground(ctx, mx, my, d);
        super.render(ctx, mx, my, d);
        String name = schematics.isEmpty() ? "(none)"
            : schematics.get(Math.floorMod(scroll, schematics.size())).getName();
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(name), width / 2, height - 40, 0xFFFFFF);
    }
}
