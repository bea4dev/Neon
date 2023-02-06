package com.github.bea4dev.neon.texture;

import com.github.bea4dev.neon.Neon;
import org.bukkit.Material;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class BlockTextureLoader {

    public static void load() throws Exception {
        for (Material material : Material.values()) {
            String materialString = material.toString();
            if (!material.isBlock() || material.isAir() || !material.isSolid() || material.isLegacy()
                    || materialString.endsWith("_PRESSURE_PLATE") || material.isEdible() || material.isInteractable()
                    || materialString.contains("GLASS") || materialString.contains("HEAD") || materialString.contains("BANNER")) { continue; }

            String materialName = material.getKey().getKey().replace("minecraft:", "");

            if (material != Material.PETRIFIED_OAK_SLAB) {
                if (materialString.endsWith("_STAIRS") || materialString.endsWith("_SLAB") || materialString.endsWith("_WALL") || materialString.startsWith("INFESTED_")) {
                    Material textureMaterial;
                    String removeStairsName = material.toString().replace("_STAIRS", "").replace("_SLAB", "").replace("_WALL", "").replace("INFESTED_", "");

                    try {
                        textureMaterial = Material.valueOf(removeStairsName);
                    } catch (Exception e1) {
                        try {
                            textureMaterial = Material.valueOf(removeStairsName + "_BLOCK");
                        } catch (Exception e2) {
                            try {
                                textureMaterial = Material.valueOf(removeStairsName + "_PLANKS");
                            } catch (Exception e3) {
                                textureMaterial = Material.valueOf(removeStairsName + "S");
                            }
                        }
                    }

                    materialName = textureMaterial.getKey().getKey().replace("minecraft:", "");
                }

                if (material.toString().endsWith("_WOOD")) {
                    Material textureMaterial;
                    String logName = material.toString().replace("_WOOD", "_LOG");
                    textureMaterial = Material.valueOf(logName);
                    materialName = textureMaterial.getKey().getKey().replace("minecraft:", "");
                }
            }

            File file = new File("plugins/NeonGenerator/textures/block/" + materialName + ".png");
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                Neon.getPlugin().getLogger().info("Texture for " + materialName + " is not found.");
                continue;
            }

            Neon.getPlugin().getLogger().info("Loading " + materialName + ".png");

            BufferedImage image = ImageIO.read(file);
            double redSum = 0.0;
            double greenSum = 0.0;
            double blueSum = 0.0;
            int count = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    Color color = new Color(image.getRGB(x, y));
                    if (color.getAlpha() != 0) {
                        redSum += (double) color.getRed();
                        greenSum += (double) color.getGreen();
                        blueSum += (double) color.getBlue();
                        count++;
                    }
                }
            }
            int redAverage = (int) (redSum / (double) count);
            int greenAverage = (int) (greenSum / (double) count);
            int blueAverage = (int) (blueSum / (double) count);

            float[] hsvArray = Color.RGBtoHSB(redAverage, greenAverage, blueAverage, null);

            //System.out.println(material + " : " + redAverage + ", " + greenAverage + ", " + blueAverage);

            RGB rgb = new RGB(redAverage, greenAverage, blueAverage);
            HSV hsv = new HSV(hsvArray[0], hsvArray[1], hsvArray[2]);

            BlockTextureInfo textureInfo = new BlockTextureInfo(material, rgb, hsv);
            BlockTextureInfo.register(textureInfo);

            image.flush();
        }
    }

}
