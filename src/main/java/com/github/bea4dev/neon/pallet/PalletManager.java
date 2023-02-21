package com.github.bea4dev.neon.pallet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;

public class PalletManager {

    public static final Map<UUID, String> registerNameMap = new HashMap<>();

    public static boolean register(Player player, Block block) {
        String name = registerNameMap.get(player.getUniqueId());
        if (name == null) {
            return false;
        }

        List<Material> list = new ArrayList<>();
        Material material = block.getType();
        while (!material.isAir()) {
            list.add(material);
            block = block.getRelative(BlockFace.UP);
            material = block.getType();
        }

        Pallet pallet = new Pallet(name, list);
        Pallet.register(pallet);

        player.sendMessage(ChatColor.AQUA + "Success!");

        registerNameMap.remove(player.getUniqueId());

        return true;
    }

}
