package com.github.bea4dev.neon;

import com.github.bea4dev.neon.generator.ScriptFunctionHolder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.contan_lang.variables.primitive.ContanFunctionExpression;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NeonAPI {

    public static final Map<String, ScriptFunctionHolder> functionMap = new HashMap<>();

    public static void registerFunction(String name, Material icon, ContanFunctionExpression function) {
        ItemStack iconItem = new ItemStack(icon);
        ItemMeta meta = iconItem.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(name);
        iconItem.setItemMeta(meta);

        functionMap.put(name, new ScriptFunctionHolder(iconItem, function));
    }



}
