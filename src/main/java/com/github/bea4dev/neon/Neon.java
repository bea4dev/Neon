package com.github.bea4dev.neon;

import org.bukkit.plugin.java.JavaPlugin;

public final class Neon extends JavaPlugin {

    private static Neon plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Neon getPlugin() {return plugin;}

}
