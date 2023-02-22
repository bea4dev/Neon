package com.github.bea4dev.neon;

import com.github.bea4dev.neon.generator.BlockInfo;
import com.github.bea4dev.neon.generator.Generator;
import com.github.bea4dev.neon.generator.ScriptFunctionHolder;
import com.github.bea4dev.neon.pallet.Pallet;
import com.github.bea4dev.neon.texture.BlockTextureInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.contan_lang.runtime.JavaContanFuture;
import org.contan_lang.variables.ContanObject;
import org.contan_lang.variables.primitive.ContanBoolean;
import org.contan_lang.variables.primitive.ContanFunctionExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thpmc.vanilla_source.api.VanillaSourceAPI;
import thpmc.vanilla_source.api.contan.ContanUtil;
import thpmc.vanilla_source.api.entity.tick.TickThread;

import java.util.Collection;
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

    public static @Nullable BlockTextureInfo findBlockRGB(int r, int g, int b) {
        return BlockTextureInfo.findBlockRGB(r, g, b, material -> true);
    }

    public static @Nullable BlockTextureInfo findBlockHSV(double h, double s, double v) {
        return BlockTextureInfo.findBlockHSV((float) h, (float) s, (float) v, material -> true);
    }

    public static @Nullable BlockTextureInfo findNormalBlockRGB(int r, int g, int b) {
        return BlockTextureInfo.findBlockRGB(r, g, b, Material::isOccluding);
    }

    public static @Nullable BlockTextureInfo findNormalBlockHSV(double h, double s, double v) {
        return BlockTextureInfo.findBlockHSV((float) h, (float) s, (float) v, Material::isOccluding);
    }

    public static @Nullable Pallet getPallet(@NotNull String name) {
        return Pallet.getPallet(name);
    }

    public static Collection<Object> filterIsSolid(Collection<Object> collection) {
        collection.removeIf(object -> {
            if (object instanceof BlockInfo) {
                BlockInfo block = (BlockInfo) object;
                Material material = block.getMaterial();
                return material != null && !material.isSolid();
            }
            return false;
        });
        return collection;
    }

    public static Collection<Object> applyPalletForVisualHeightLevel(String palletName, Collection<Object> collection) {
        Pallet pallet = getPallet(palletName);
        if (pallet == null) { throw new IllegalArgumentException("Pallet '" + palletName + "' is not found."); }
        if (pallet.list.size() != 16) { throw new IllegalArgumentException("Pallet '" + palletName + "' is not 16 in length."); }

        filterIsSolid(collection);

        for (Object object : collection) {
            if (!(object instanceof BlockInfo)) { continue; }

            BlockInfo block = (BlockInfo) object;
            int heightLevel = block.getHeightLevel();
            if (heightLevel >= 0 && block.isEdited()) {
                Material material = pallet.list.get(heightLevel);
                block.setMaterial(material);
            }
        }
        return collection;
    }

    public static ContanObject<?> writeAll(Collection<Object> collection) {
        JavaContanFuture future = ContanUtil.createFutureInstance();

        Bukkit.getScheduler().runTask(Neon.getPlugin(), () -> {
            try {
                for (Object object : collection) {
                    if (!(object instanceof BlockInfo)) {
                        continue;
                    }
                    BlockInfo block = (BlockInfo) object;
                    block.write();
                }
            } catch (Exception e) {
                e.printStackTrace();
                future.complete(new ContanBoolean(VanillaSourceAPI.getInstance().getContanEngine(), false));
                return;
            }
            future.complete(new ContanBoolean(VanillaSourceAPI.getInstance().getContanEngine(), true));
        });

        return future.getContanInstance();
    }

}
