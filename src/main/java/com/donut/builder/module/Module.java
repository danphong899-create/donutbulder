package com.donut.builder.module;

public abstract class Module {
    public final String name;
    public boolean enabled;

    public Module(String name) { this.name = name; }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public abstract void onEnable();
    public abstract void onDisable();
    public abstract void onTick();
}
