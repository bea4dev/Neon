package com.github.bea4dev.neon.command;

import com.github.bea4dev.neon.editor.BrushEditor;
import com.github.bea4dev.neon.pallet.PalletManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.TextArgument;
import org.bukkit.ChatColor;


public class NeonCommand {

    public static void register() {

        new CommandAPICommand("neon").withSubcommands(
                new CommandAPICommand("brush")
                        .executesPlayer((sender, args) -> { BrushEditor.openBrushMainMenu(sender); }),

                new CommandAPICommand("pallet").withSubcommands(
                        new CommandAPICommand("register")
                                .withArguments(new TextArgument("name"))
                                .executesPlayer((sender, args) -> {
                                    if (PalletManager.registerNameMap.containsKey(sender.getUniqueId())) {
                                        sender.sendMessage(ChatColor.RED + "The specified name already exists.");
                                        return;
                                    }
                                    PalletManager.registerNameMap.put(sender.getUniqueId(), (String) args[0]);

                                    sender.sendMessage(ChatColor.AQUA + "Click on the palette block you wish to register.");
                                })
                )
        ).register();

    }

}
