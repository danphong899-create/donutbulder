package com.donut.builder;

import com.donut.builder.gui.HUD;
import com.donut.builder.gui.MainMenu;
import com.donut.builder.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class DonutBuilderMod implements ClientModInitializer {
    public static KeyBinding openMenu;
    public static KeyBinding toggleBuilder;

    @Override
    public void onInitializeClient() {
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.donutbuilder.menu", InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT, "category.donutbuilder"));
        toggleBuilder = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.donutbuilder.toggle", InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B, "category.donutbuilder"));

        ModuleManager.init();
        HUD.register();

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openMenu.wasPressed()) mc.setScreen(new MainMenu());
            while (toggleBuilder.wasPressed()) {
                var ab = ModuleManager.get("autobuilder");
                if (ab != null) ab.toggle();
            }
            ModuleManager.tickAll();
        });
    }
}
