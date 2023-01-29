package com.github.bea4dev.neon.generator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thpmc.vanilla_source.api.entity.tick.TickThread;
import thpmc.vanilla_source.api.util.BlockPosition3i;
import thpmc.vanilla_source.api.world.cache.EngineWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockInfo {

    public final TickThread thread;
    public final Generator generator;
    public final EngineWorld world;
    public final BlockPosition3i position;

    public @Nullable BlockData data = null;
    public Map<String, Object> tag = new HashMap<>();
    private BlockData originalData;

    public BlockInfo(TickThread thread, Generator generator, String worldName, BlockPosition3i position) {
        this.thread = thread;
        this.generator = generator;
        this.position = position;
        this.world = thread.getThreadLocalCache().getGlobalWorld(worldName);
        this.tag.put("ground", false);

        read();
    }

    public void read() {
        BlockData blockData = world.getBlockData(position.getX(), position.getY(), position.getZ());
        if (blockData == null) {
            this.data = Material.AIR.createBlockData();
        } else {
            this.data = blockData;
        }

        if (originalData == null) {
            originalData = blockData;
        }
    }

    public void write() {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("This operation is not allowed on asynchronous threads.");
        }
        World bukkitWorld = Bukkit.getWorld(world.getName());
        if (bukkitWorld == null) {
            throw new IllegalStateException("World '" + world.getName() + "' does not exist.");
        }
        bukkitWorld.getBlockAt(position.getX(), position.getY(), position.getZ()).setBlockData(this.data);
    }

    public BlockData getBlockData() {return this.data;}

    public void setBlockData(BlockData blockData) {
        if (blockData == null) {
            blockData = Material.AIR.createBlockData();
        }
        this.data = blockData;
    }

    public void setMaterial(Material material) {
        if (material == null) {
            material = Material.AIR;
        }
        this.setBlockData(material.createBlockData());
    }

    public void setMaterial(String materialName) {this.setMaterial(Material.matchMaterial(materialName));}

    public BlockData getOriginalData() {return originalData;}

    public double getGroundFlatnessFactor() {
        return getGroundFlatnessFactor(3);
    }

    public double getGroundFlatnessFactor(int kernelSize) {
        int positionX = position.getX();
        int positionY = position.getY();
        int positionZ = position.getZ();
        double flatness = 0.0;
        int numberOfBlocks = 0;
        for (int x = positionX - kernelSize; x <= positionX + kernelSize; x++) {
            for (int z = positionZ - kernelSize; z <= positionZ + kernelSize; z++) {
                BlockInfo block = generator.getBlock(x, positionY, z);
                if (block == null) {
                    continue;
                }
                BlockInfo exposed = block.getUpExposedBlock();
                flatness += Math.abs(positionY - exposed.position.getY());
                numberOfBlocks++;
            }
        }
        return flatness / (double) numberOfBlocks;
    }

    public boolean isUpExposed() {
        if (data != null && !data.getMaterial().isSolid()) {
            return true;
        }
        BlockInfo upBlock = generator.getBlock(position.getX(), position.getY() + 1, position.getZ());
        if (upBlock == null) {
            return true;
        }
        BlockData upBlockData = upBlock.getBlockData();
        if (upBlockData == null) {
            return true;
        } else {
            return !upBlockData.getMaterial().isSolid();
        }
    }

    public boolean isDownExposed() {
        if (data != null && !data.getMaterial().isSolid()) {
            return false;
        }
        BlockInfo downBlock = generator.getBlock(position.getX(), position.getY() - 1, position.getZ());
        if (downBlock == null) {
            return true;
        }
        BlockData downBlockData = downBlock.getBlockData();
        if (downBlockData == null) {
            return true;
        } else {
            return !downBlockData.getMaterial().isSolid();
        }
    }

    public boolean isExposedYAxis() {return isUpExposed() || isDownExposed();}

    public @NotNull BlockInfo getUpExposedBlock() {
        BlockInfo currentBlock = this;
        while (true) {
            if (currentBlock.isUpExposed()) {
                break;
            }
            BlockPosition3i position = currentBlock.position;
            BlockInfo upBlock = generator.getBlock(position.getX(), position.getY() + 1, position.getZ());
            if (upBlock == null) {
                break;
            }
            currentBlock = upBlock;
        }
        return currentBlock;
    }

    public @NotNull BlockInfo getDownExposedBlock() {
        BlockInfo currentBlock = this;
        while (true) {
            if (currentBlock.isDownExposed()) {
                break;
            }
            BlockPosition3i position = currentBlock.position;
            BlockInfo downBlock = generator.getBlock(position.getX(), position.getY() - 1, position.getZ());
            if (downBlock == null) {
                break;
            }
            currentBlock = downBlock;
        }
        return currentBlock;
    }

    public @NotNull BlockInfo getExposedBlockYAxis() {
        BlockInfo upExposedBlock = getUpExposedBlock();
        BlockInfo downExposedBlock = getDownExposedBlock();
        int upY = upExposedBlock.position.getY();
        int downY = downExposedBlock.position.getY();
        int y = position.getY();
        if (upY - y < y - downY) {
            return upExposedBlock;
        } else {
            return downExposedBlock;
        }
    }

    public List<BlockInfo> getSquare(int radius) {
        List<BlockInfo> list = new ArrayList<>();
        for (int x = position.getX() - radius; x <= position.getX() + radius; x++) {
            for (int y = position.getY() - radius; y <= position.getY() + radius; y++) {
                for (int z = position.getZ() - radius; z <= position.getZ() + radius; z++) {
                    BlockInfo block = generator.getBlock(x, y, z);
                    if (block != null) {
                        list.add(block);
                    }
                }
            }
        }
        return list;
    }

    public List<BlockInfo> getSphere(int radius) {
        List<BlockInfo> square = this.getSquare(radius);
        double radiusSquared = (double) radius * radius;
        List<BlockInfo> sphere = new ArrayList<>();
        for (BlockInfo block : square) {
            Vector blockPosition = new Vector(block.position.getX(), block.position.getY(), block.position.getZ());
            Vector thisPosition = new Vector(position.getX(), position.getY(), position.getZ());
            if (blockPosition.distanceSquared(thisPosition) < radiusSquared) {
                sphere.add(block);
            }
        }
        return sphere;
    }

}
