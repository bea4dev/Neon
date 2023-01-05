package com.github.bea4dev.neon.generator;

import org.bukkit.inventory.ItemStack;
import org.contan_lang.variables.primitive.ContanFunctionExpression;

public class ScriptFunctionHolder {

    public final ItemStack icon;
    public final ContanFunctionExpression function;

    public ScriptFunctionHolder(ItemStack icon, ContanFunctionExpression function) {
        this.icon = icon;
        this.function = function;
    }

}
