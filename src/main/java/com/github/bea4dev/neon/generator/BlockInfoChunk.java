package com.github.bea4dev.neon.generator;

import org.bukkit.World;
import thpmc.vanilla_source.api.entity.tick.TickThread;
import thpmc.vanilla_source.api.util.BlockPosition3i;

import java.util.*;

public class BlockInfoChunk {

    public final TickThread thread;
    public final World world;
    public final int chunkX;
    public final int chunkZ;

    private final Map<BlockPosition3i, BlockInfo> blockInfoMap = new HashMap<>();

    public BlockInfoChunk(TickThread thread, World world, int chunkX, int chunkZ) {
        this.thread = thread;
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public BlockInfo getOrCreateBlockInfo(BlockPosition3i position) {
        return blockInfoMap.computeIfAbsent(position, key -> new BlockInfo(thread, world.getName(), position));
    }

    public Set<BlockInfo> getAllBlocks() {
        Set<BlockInfo> set = new HashSet<>();
        for (Map.Entry<BlockPosition3i, BlockInfo> entry : blockInfoMap.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

}
