package com.github.bea4dev.neon.command;

import com.github.bea4dev.neon.editor.BrushEditor;
import net.propromp.neocommander.api.annotation.Command;
import net.propromp.neocommander.api.annotation.Sender;
import org.bukkit.entity.Player;

@Command(name = "neon", permission = "neon.command.neon")
public class NeonCommand {

    @Command(name = "brush", description = "Open brush gui")
    public void brush(@Sender Player sender) {
        BrushEditor.openBrushMainMenu(sender);
    }

}
