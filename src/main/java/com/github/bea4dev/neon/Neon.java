package com.github.bea4dev.neon;

import com.github.bea4dev.neon.command.CommandRegistry;
import com.github.bea4dev.neon.editor.Brush;
import com.github.bea4dev.neon.pallet.Pallet;
import com.github.bea4dev.neon.texture.BlockTextureLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.contan_lang.syntax.exception.ContanParseException;

import java.io.IOException;

public final class Neon extends JavaPlugin {

    private static Neon plugin;

    @Override
    public void onLoad() {
        super.onLoad();
        CommandRegistry.onLoad();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        //Load textures
        try {
            BlockTextureLoader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //Load pallet
        Pallet.loadAll();

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

        //Register event listener
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new EventListener(), this);

        //Register commands
        CommandRegistry.onEnable(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            Brush.saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Save pallet
        try {
            Pallet.saveAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Unregister commands
        CommandRegistry.onDisable();
    }

    public static Neon getPlugin() {return plugin;}

}
