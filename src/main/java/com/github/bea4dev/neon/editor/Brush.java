package com.github.bea4dev.neon.editor;

import com.github.bea4dev.neon.NeonAPI;
import com.github.bea4dev.neon.generator.BlockInfo;
import com.github.bea4dev.neon.generator.Generator;
import com.github.bea4dev.neon.generator.ScriptFunctionHolder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.contan_lang.ContanEngine;
import org.contan_lang.ContanModule;
import org.contan_lang.syntax.exception.ContanParseException;
import org.jetbrains.annotations.Nullable;
import thpmc.vanilla_source.api.VanillaSourceAPI;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class Brush {

    private static final Map<String, Brush> brushMap = new HashMap<>();

    private static ContanModule contanModule;

    public static void initialize() throws ContanParseException {
        ContanEngine contanEngine = VanillaSourceAPI.getInstance().getContanEngine();
        contanModule = contanEngine.compile(".neon_internal", "" +
                "function run(generator, list, player, block) {\n" +
                "    all holder in list {\n" +
                "        data expression = holder.expression\n" +
                "        expression(generator, player, block)\n" +
                "    }\n" +
                "}");

        loadAll();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadAll() {
        File dir = new File("plugins/NeonGenerator/brush");

        dir.getParentFile().mkdir();
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files != null) {
            if (files.length == 0) {
                files = dir.listFiles();
            }
        }

        if (files != null) {
            for (File file : files) {
                if (!file.getName().contains(".yml")) {
                    continue;
                }

                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

                String id = file.getName().replace(".yml", "");
                String displayName = yml.getString("display-name");
                List<String> description = yml.getStringList("description");
                List<String> functionIdList = yml.getStringList("function-id-list");
                Material icon = Material.matchMaterial(Objects.requireNonNull(yml.getString("icon-material")));
                brushMap.put(id, new Brush(id, displayName, description, functionIdList, icon));
            }
        }
    }

    public static void saveAll() throws IOException {
        for (Brush brush : brushMap.values()) {
            brush.save();
        }
    }

    public static @Nullable Brush getBrush(String id) {return brushMap.get(id);}

    public static Collection<Brush> getAllBrushes() {return brushMap.values();}

    public static void register(Brush brush) {brushMap.put(brush.id, brush);}




    public final String id;
    public final String displayName;
    private final List<String> description;
    public final List<String> functionIdList;
    private ItemStack itemStack;

    public Brush(String id, String displayName, List<String> description, List<String> functionIdList, Material icon) {
        this.id = id;
        this.displayName = displayName;
        description.add("");
        description.add(ChatColor.RESET.toString() + ChatColor.GRAY + id);
        this.description = description;
        this.functionIdList = functionIdList;
        this.setIcon(icon);
    }

    public ItemStack getItem() {return itemStack;}

    public void run(Player player, Block block) throws ExecutionException, InterruptedException {
        Generator generator = NeonAPI.getGenerator(block);
        List<ScriptFunctionHolder> holderList = new ArrayList<>();
        for (String functionId : functionIdList) {
            holderList.add(NeonAPI.functionMap.get(functionId));
        }
        BlockInfo center = generator.getBlock(block.getX(), block.getY(), block.getZ());
        Runnable task = () -> {
            try {
                contanModule.invokeFunction(generator.thread, "run", generator, holderList, player, center);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "ブラシ使用時にエラーが発生しました(詳細はコンソールを確認してください)");
                player.sendMessage( ChatColor.YELLOW + e.getMessage());
                e.printStackTrace();
            }
        };
        generator.thread.scheduleTask(task);
    }

    public void setIcon(Material icon) {
        this.itemStack = new ItemStack(icon);
        ItemMeta itemMeta = itemStack.getItemMeta();
        Objects.requireNonNull(itemMeta).setDisplayName(displayName);
        Objects.requireNonNull(itemMeta).setLore(description);
        itemStack.setItemMeta(itemMeta);
    }

    public void save() throws IOException {
        File file = new File("plugins/NeonGenerator/brush/" + id + ".yml");
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("display-name", displayName);
        yml.set("function-id-list", functionIdList);
        yml.set("icon-material", itemStack.getType().getKey().getKey());
        yml.save(file);
    }

}
