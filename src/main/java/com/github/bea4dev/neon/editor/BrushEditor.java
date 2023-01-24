package com.github.bea4dev.neon.editor;

import be4rjp.artgui.button.*;
import be4rjp.artgui.frame.Artist;
import be4rjp.artgui.menu.ArtMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import thpmc.vanilla_source.api.VanillaSourceAPI;

import java.util.function.Consumer;

public class BrushEditor {

    public static void openBrushSelectGUI(Player player, Consumer<Brush> consumer) {
        Artist artist = new Artist(() -> {
            ArtButton V = null;
            ArtButton G = new ArtButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("&a").build());

            //ページ移動用ボタンを作成
            PageNextButton N = new PageNextButton(new ItemBuilder(Material.ARROW).name("&r次のページ &7[{NextPage}/{MaxPage}]").build());

            //ページ移動用ボタンを作成
            PageBackButton P = new PageBackButton(new ItemBuilder(Material.ARROW).name("&r前のページ &7[{PreviousPage}/{MaxPage}]").build());

            //戻るボタンを作成
            MenuBackButton B = new MenuBackButton(new ItemBuilder(Material.OAK_DOOR).name("&r{PreviousName}&7に戻る").build());

            //現在のページを表示するボタンを作成
            ReplaceableButton I = new ReplaceableButton(new ItemBuilder(Material.NAME_TAG).name("&7現在のページ&r[{CurrentPage}/{MaxPage}]").build());

            ArtButton Q = new ArtButton(new ItemBuilder(Material.BARRIER).name("&c&n閉じる").build());
            Q.listener((event, menu) -> player.closeInventory());

            return new ArtButton[]{
                    V, V, V, V, V, V, V, G, N,
                    V, V, V, V, V, V, V, G, I,
                    V, V, V, V, V, V, V, G, P,
                    V, V, V, V, V, V, V, G, G,
                    V, V, V, V, V, V, V, G, Q,
                    V, V, V, V, V, V, V, G, B,
            };
        });

        ArtMenu artMenu = artist.createMenu(VanillaSourceAPI.getInstance().getArtGUI(), "&r&nブラシを選択");

        artMenu.asyncCreate(menu -> {
            for (Brush brush : Brush.getAllBrushes()) {
                ArtButton button = new ArtButton(brush.getItem());
                button.listener((event, m) -> consumer.accept(brush));
                menu.addButton(button);
            }
        });

        artMenu.open(player);
    }

    public static void openBrushMainMenu(Player player) {
        openBrushSelectGUI(player, brush -> openBrushInfoMenu(player, brush));
    }

    public static void openBrushInfoMenu(Player player, Brush brush) {
        Artist artist = new Artist(() -> {
            ArtButton G = new ArtButton(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("&a").build());

            //戻るボタンを作成
            MenuBackButton B = new MenuBackButton(new ItemBuilder(Material.OAK_DOOR).name("&r{PreviousName}&7に戻る").build());

            ArtButton T = new ArtButton(new ItemBuilder(brush.getItem().getType()).name("&b&n入手する").build());
            T.listener((event, menu) -> {
                player.sendMessage(ChatColor.GREEN.toString() + ChatColor.UNDERLINE + "ブラシ'" + brush.id + "'を入手しました");
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
                player.getInventory().addItem(brush.getItem());
            });

            ArtButton E = new ArtButton(new ItemBuilder(Material.COMMAND_BLOCK).name("&b&nフィルタ関数の順序を編集").build());
            E.listener((event, menu) -> {

            });

            ArtButton Q = new ArtButton(new ItemBuilder(Material.BARRIER).name("&c&n閉じる").build());
            Q.listener((event, menu) -> player.closeInventory());

            return new ArtButton[]{
                    G, G, G, G, G, G, G, G, G,
                    G, G, T, G, G, G, E, G, Q,
                    G, G, G, G, G, G, G, G, B,
            };
        });

        ArtMenu artMenu = artist.createMenu(VanillaSourceAPI.getInstance().getArtGUI(), "&nブラシ編集");
        artMenu.asyncCreate(menu -> {});

        artMenu.open(player);
    }

}
