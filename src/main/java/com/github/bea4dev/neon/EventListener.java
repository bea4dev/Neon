package com.github.bea4dev.neon;

import com.github.bea4dev.neon.editor.Brush;
import com.github.bea4dev.neon.pallet.PalletManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    public void onItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) {
                return;
            }

            List<String> lore = itemMeta.getLore();
            if (lore == null || lore.isEmpty()) {
                return;
            }

            String id = ChatColor.stripColor(lore.get(lore.size() - 1));
            Brush brush = Brush.getBrush(id);

            if (brush == null) {
                return;
            }

            RayTraceResult result = player.rayTraceBlocks(50.0);
            Block block;
            if (result == null || (block = result.getHitBlock()) == null) {
                player.sendMessage(ChatColor.RED + "視点の先にブロックが見つかりませんでした");
                return;
            }

            try {
                brush.run(player, block);
            } catch (Exception e) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                player.sendMessage(ChatColor.RED + "ブラシ処理実行時にエラーが発生しました");
                player.sendMessage(ChatColor.YELLOW + stringWriter.toString());
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        if (PalletManager.register(player, event.getBlock())) {
            event.setCancelled(true);
        }
    }

}
