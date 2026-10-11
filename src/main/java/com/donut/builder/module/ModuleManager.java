package com.donut.builder.module;

import com.donut.builder.module.builder.AutoBuilder;
import com.donut.builder.module.misc.AreaCleaner;
import com.donut.builder.module.inventory.AutoSeller;

import java.util.*;

public class ModuleManager {
    private static final Map<String, Module> modules = new LinkedHashMap<>();

    public static void init() {
        register(new AutoBuilder());
        register(new AreaCleaner());
        register(new AutoSeller());
    }

    public static void register(Module m) { modules.put(m.name.toLowerCase(Locale.ROOT), m); }
    public static Module get(String name) { return modules.get(name.toLowerCase(Locale.ROOT)); }
    public static void tickAll() { for (Module m : modules.values()) if (m.enabled) m.onTick(); }
    public static Collection<Module> all() { return modules.values(); }
}
