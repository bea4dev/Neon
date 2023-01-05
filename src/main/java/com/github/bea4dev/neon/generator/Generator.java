package com.github.bea4dev.neon.generator;

import org.bukkit.World;
import org.bukkit.util.Vector;
import thpmc.vanilla_source.api.entity.tick.TickThread;
import thpmc.vanilla_source.api.util.BlockPosition3i;
import thpmc.vanilla_source.api.world.ChunkUtil;

import java.util.*;

public class Generator {

    public final TickThread thread;
    private final World world;
    private Vector rangeMin;
    private Vector rangeMax;

    private final Map<Long, BlockInfoChunk> blockInfoMap = new HashMap<>();

    public Generator(TickThread thread, World world, Vector rangePos1, Vector rangePos2) {
        this.thread = thread;
        this.world = world;
        this.rangeMin = Vector.getMinimum(rangePos1, rangePos2);
        this.rangeMax = Vector.getMaximum(rangePos1, rangePos2);
    }

    public BlockInfo getBlock(int x, int y, int z) {
        BlockInfoChunk chunk = blockInfoMap.computeIfAbsent(ChunkUtil.getChunkKey(x, y), key -> new BlockInfoChunk(thread, world, x >> 4, z >> 4));
        return chunk.getOrCreateBlockInfo(new BlockPosition3i(x, y, z));
    }

    public Set<BlockInfo> getAllBlocks() {
        Set<BlockInfo> set = new HashSet<>();
        for (BlockInfoChunk chunk : blockInfoMap.values()) {
            set.addAll(chunk.getAllBlocks());
        }
        return set;
    }

    public Set<BlockInfo> getAllGroundBlocks() {
        Set<BlockInfo> set = getAllBlocks();
        set.removeIf(blockInfo -> !blockInfo.isGround());
        return set;
    }

}
