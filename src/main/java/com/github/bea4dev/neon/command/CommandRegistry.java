package com.github.bea4dev.neon.command;

import com.github.bea4dev.neon.Neon;
import net.propromp.neocommander.api.CommandManager;
import net.propromp.neocommander.api.annotation.AnnotationManager;

public class CommandRegistry {

    private static CommandManager commandManager;

    public static void register(Neon plugin) {
        commandManager = new CommandManager(plugin);
        AnnotationManager annotationManager = commandManager.getAnnotationManager();
        annotationManager.register(new NeonCommand());
    }

    public static void unregister() {
        if (commandManager != null) {
            commandManager.clearCommands();
        }
    }

}
