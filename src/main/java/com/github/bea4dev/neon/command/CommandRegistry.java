package com.github.bea4dev.neon.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import org.bukkit.plugin.Plugin;

public class CommandRegistry {

    public static void onLoad() {
        CommandAPI.onLoad(new CommandAPIConfig().verboseOutput(true));
        NeonCommand.register();
    }

    public static void onEnable(Plugin plugin) {
        CommandAPI.onEnable(plugin);
    }

    public static void onDisable() {
        CommandAPI.onDisable();
    }

}
