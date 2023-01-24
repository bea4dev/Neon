package com.github.bea4dev.neon;

import com.github.bea4dev.neon.generator.Generator;
import com.github.bea4dev.neon.generator.ScriptFunctionHolder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.contan_lang.variables.primitive.ContanFunctionExpression;
import thpmc.vanilla_source.api.VanillaSourceAPI;
import thpmc.vanilla_source.api.entity.tick.TickThread;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NeonAPI {

    public static final Map<String, ScriptFunctionHolder> functionMap = new HashMap<>();

    private static final Map<String, Generator> generatorMap = new HashMap<>();

    private static Vector defaultGeneratorRangeSize = new Vector(100, 100, 100);

    public static void registerFunction(String name, Material icon, ContanFunctionExpression function) {
        ItemStack iconItem = new ItemStack(icon);
        ItemMeta meta = iconItem.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(name);
        iconItem.setItemMeta(meta);

        functionMap.put(name, new ScriptFunctionHolder(iconItem, function));
    }

    public static Generator getGenerator(Block block) {
        TickThread thread = VanillaSourceAPI.getInstance().getTickThreadPool().getNextTickThread();
        int sizeX = defaultGeneratorRangeSize.getBlockX();
        int sizeY = defaultGeneratorRangeSize.getBlockY();
        int sizeZ = defaultGeneratorRangeSize.getBlockZ();
        Vector add = new Vector(sizeX / 2, sizeY / 2, sizeZ / 2);
        Vector minPos = block.getLocation().toVector().add(add.multiply(-1));
        Vector maxPos = block.getLocation().toVector().add(add);
        return generatorMap.computeIfAbsent(block.getWorld().getName(), key -> new Generator(thread, block.getWorld(), minPos, maxPos));
    }

}
