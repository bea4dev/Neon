package com.github.bea4dev.neon.generator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import thpmc.vanilla_source.api.entity.tick.TickThread;
import thpmc.vanilla_source.api.util.BlockPosition3i;
import thpmc.vanilla_source.api.world.cache.EngineWorld;

public class BlockInfo {

    public final TickThread thread;
    public final EngineWorld world;
    public final BlockPosition3i position;

    private BlockData blockData;
    private boolean isGround = false;
    public String tag = "NULL";

    public BlockInfo(TickThread thread, String worldName, BlockPosition3i position) {
        this.thread = thread;
        this.position = position;
        this.world = thread.getThreadLocalCache().getGlobalWorld(worldName);

        read();
    }

    public void read() {
        BlockData blockData = world.getBlockData(position.getX(), position.getY(), position.getZ());
        if (blockData == null) {
            this.blockData = Material.AIR.createBlockData();
        } else {
            this.blockData = blockData;
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
        bukkitWorld.getBlockAt(position.getX(), position.getY(), position.getZ()).setBlockData(this.blockData);
    }

    public BlockData getBlockData() {
        return this.blockData;
    }

    public void setBlockData(BlockData blockData) {
        if (blockData == null) {
            blockData = Material.AIR.createBlockData();
        }
        this.blockData = blockData;
    }

    public void setMaterial(Material material) {
        if (material == null) {
            material = Material.AIR;
        }
        this.setBlockData(material.createBlockData());
    }

    public void setMaterial(String materialName) {
        this.setMaterial(Material.matchMaterial(materialName));
    }

    public void markAsGround() {
        this.isGround = true;
    }

    public void setGround(boolean ground) {isGround = ground;}

    public boolean isGround() {return isGround;}

}
