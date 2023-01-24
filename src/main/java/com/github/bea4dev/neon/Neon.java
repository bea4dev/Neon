package com.github.bea4dev.neon;

import com.github.bea4dev.neon.editor.Brush;
import org.bukkit.plugin.java.JavaPlugin;
import org.contan_lang.syntax.exception.ContanParseException;

import java.io.IOException;

public final class Neon extends JavaPlugin {

    private static Neon plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        //Load scripts
        try {
            Script.loadAllModules();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load scripts.");
        }

        //Load brushes
        try {
            Brush.initialize();
        } catch (ContanParseException e) {
            throw new RuntimeException(e);
        }
        Brush.loadAll();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            Brush.saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Neon getPlugin() {return plugin;}

}
