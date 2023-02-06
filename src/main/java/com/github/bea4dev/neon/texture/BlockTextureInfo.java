package com.github.bea4dev.neon.texture;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class BlockTextureInfo {

    private static final Map<Material, BlockTextureInfo> textureInfoMap = new EnumMap<>(Material.class);

    public static void register(BlockTextureInfo blockTextureInfo) {
        textureInfoMap.put(blockTextureInfo.material, blockTextureInfo);
    }

    public static @Nullable BlockTextureInfo getTextureInfo(Material material) {
        return textureInfoMap.get(material);
    }

    public static @Nullable BlockTextureInfo findBlockRGB(int r, int g, int b) {
        BlockTextureInfo currentTexture = null;
        int currentMinDifference = Integer.MAX_VALUE;
        for (BlockTextureInfo textureInfo : textureInfoMap.values()) {
            RGB rgb = textureInfo.rgb;
            int difference = Math.abs(rgb.r - r) + Math.abs(rgb.g - g) + Math.abs(rgb.b - b);
            if (difference < currentMinDifference) {
                currentMinDifference = difference;
                currentTexture = textureInfo;
            }
        }
        return currentTexture;
    }

    public static @Nullable BlockTextureInfo findBlockHSV(float h, float s, float v) {
        BlockTextureInfo currentTexture = null;
        float currentMinDifference = Float.MAX_VALUE;
        for (BlockTextureInfo textureInfo : textureInfoMap.values()) {
            HSV hsv = textureInfo.hsv;
            float difference = Math.abs(hsv.h - h) + Math.abs(hsv.s - s) + Math.abs(hsv.v - v);
            if (difference < currentMinDifference) {
                currentMinDifference = difference;
                currentTexture = textureInfo;
            }
        }
        return currentTexture;
    }



    public final Material material;

    public final RGB rgb;
    public final HSV hsv;

    public BlockTextureInfo(Material material, RGB rgb, HSV hsv) {
        this.material = material;
        this.rgb = rgb;
        this.hsv = hsv;
    }

}
