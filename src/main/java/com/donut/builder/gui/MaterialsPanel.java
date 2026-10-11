package com.donut.builder.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class MaterialsPanel extends Screen {
    public MaterialsPanel() { super(Text.literal("Materials")); }

    @Override
    protected void init() {
        TextFieldWidget field = new TextFieldWidget(textRenderer, width / 2 - 100, 60, 200, 20, Text.literal("slot"));
        field.setPlaceholder(Text.literal("shulker inventory slot index"));
        addDrawableChild(field);

        addDrawableChild(ButtonWidget.builder(Text.literal("Extract All"), b -> {
            try {
                int slot = Integer.parseInt(field.getText());
                new com.donut.builder.module.inventory.ShulkerExtractor().openShulkerInInventory(slot);
            } catch (Exception ignored) {}
        }).dimensions(width / 2 - 100, 90, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Back"),
            b -> client.setScreen(new MainMenu()))
            .dimensions(width / 2 - 100, 120, 200, 20).build());
    }
}
