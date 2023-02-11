package com.github.bea4dev.neon.pallet;

import com.github.bea4dev.neon.Neon;
import com.github.bea4dev.neon.texture.BlockTextureInfo;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import thpmc.vanilla_source.api.util.math.EasingBezier2D;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Pallet {

    private static final Map<String, Pallet> palletMap = new HashMap<>();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void loadAll() {
        File dir = new File("plugins/NeonGenerator/pallet");

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
                Pallet pallet = new Pallet(id);
                pallet.load(yml);

                palletMap.put(id, pallet);
            }
        }
    }


    public static void saveAll() throws IOException {
        for (Pallet pallet : palletMap.values()) {
            pallet.save();
        }
    }



    public final String id;
    public final List<Material> list = new ArrayList<>();

    public Pallet(String id) {this.id = id;}


    public @NotNull Material up(@NotNull Material material) {
        int index = list.indexOf(material);
        if (index == -1 || index - 1 >= list.size()) {
            return material;
        } else {
            return list.get(index + 1);
        }
    }

    public @NotNull Material down(@NotNull Material material) {
        int index = list.indexOf(material);
        if (index == -1 || index >= list.size() || index == 0) {
            return material;
        } else {
            return list.get(index - 1);
        }
    }

    public List<Material> getGradation(int size, double x2, double y2, double x3, double y3) {
        EasingBezier2D curve = new EasingBezier2D(x2, y2, x3, y3);
        int maxIndex = list.size() - 1;

        List<Material> gradationList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double t = (double) i / (double) (size - 1);
            double progress = curve.getProgressByTime(t);
            int index = Math.min((int) (progress * (double) maxIndex), maxIndex);
            gradationList.add(list.get(index));
        }

        return gradationList;
    }



    public void load(YamlConfiguration yml) {
        List<String> materialNameList = yml.getStringList("list");
        for (String materialName : materialNameList) {
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                Neon.getPlugin().getLogger().info("Invalid material name '" + materialName + "'.");
                continue;
            }
            list.add(material);

            BlockTextureInfo textureInfo = BlockTextureInfo.getTextureInfo(material);
            if (textureInfo != null) {
                textureInfo.pallets.add(this);
            }
        }
    }

    public void save() throws IOException {
        File file = new File("plugins/NeonGenerator/pallet/" + this.id + ".yml");
        YamlConfiguration yml = new YamlConfiguration();
        List<String> materialNameList = new ArrayList<>();
        for (Material material : this.list) {
            materialNameList.add(material.getKey().getKey());
        }
        yml.set("list", materialNameList);
        yml.save(file);
    }

}
