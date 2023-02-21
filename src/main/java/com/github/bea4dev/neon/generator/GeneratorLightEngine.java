package com.github.bea4dev.neon.generator;

import org.bukkit.Material;
import thpmc.vanilla_source.api.util.BlockPosition3i;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GeneratorLightEngine {

    private final Generator generator;

    public GeneratorLightEngine(Generator generator) {
        this.generator = generator;
    }

    public void run() {
        Set<BlockInfo> allBlocks = generator.getAllBlocks();
        Map<BlockPosition3i, BlockInfo> downExposedBlocks = new HashMap<>();

        for (BlockInfo block : allBlocks) {
            Material material = block.getMaterial();
            if (material == null || !material.isSolid()) {
                continue;
            }

            BlockInfo lowest = block.getDownExposedBlock();
            downExposedBlocks.put(lowest.position, lowest);
        }

        for (BlockInfo block : downExposedBlocks.values()) {
            int positionX = block.position.getX();
            int positionY = block.position.getY();
            int positionZ = block.position.getZ();

            int lowestY = block.position.getY();
            int highestY = block.getUpExposedBlock().position.getY();
            double diff = highestY - lowestY;
            if (diff == 0.0) {
                block.setHeightLevel(15);
                continue;
            }

            for (int y = positionY; y <= highestY; y++) {
                BlockInfo currentBlock = generator.getBlockIfExists(positionX, y, positionZ);
                if (currentBlock == null) {continue;}

                Material currentMaterial = currentBlock.getMaterial();
                if (currentMaterial == null || !currentMaterial.isSolid()) {continue;}

                int heightLevel = (int) (((double) (y - lowestY) / diff) * 15.0);
                heightLevel = Math.min(heightLevel, 15);

                currentBlock.setHeightLevel(heightLevel);
            }
        }

        for (BlockInfo block : allBlocks) {
            if (!block.isSolid()) {
                continue;
            }

            int positionX = block.position.getX();
            int positionY = block.position.getY();
            int positionZ = block.position.getZ();

            int radius = 1;
            int count = 0;
            double sum = 0;
            for (int x = positionX - radius; x <= positionX + radius; x++) {
                for (int z = positionZ - radius; z <= positionZ + radius; z++) {
                    BlockInfo currentBlock = generator.getBlockIfExists(x, positionY, z);
                    if (currentBlock == null || !currentBlock.isSolid()) {
                        continue;
                    }

                    sum += currentBlock.getHeightLevel();
                    count++;
                }
            }

            int level = (int) (sum / (double) count);

            if (block.isUpExposed()) {
                level = Math.min(level + 1, 15);
            }

            block.setVisualHeightLevel(level);
        }
    }

}
