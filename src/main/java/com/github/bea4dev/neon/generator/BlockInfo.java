package com.github.bea4dev.neon.generator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;
import thpmc.vanilla_source.api.entity.tick.TickThread;
import thpmc.vanilla_source.api.util.BlockPosition3i;
import thpmc.vanilla_source.api.world.cache.EngineWorld;

import java.util.HashMap;
import java.util.Map;

public class BlockInfo {

    public final TickThread thread;
    public final EngineWorld world;
    public final BlockPosition3i position;

    public @Nullable BlockData data = null;
    public Map<String, Object> tag = new HashMap<>();
    private BlockData originalData;

    public BlockInfo(TickThread thread, String worldName, BlockPosition3i position) {
        this.thread = thread;
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

}
