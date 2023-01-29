package com.github.bea4dev.neon.generator;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import thpmc.vanilla_source.api.entity.tick.TickThread;
import thpmc.vanilla_source.api.util.BlockPosition3i;
import thpmc.vanilla_source.api.world.ChunkUtil;

import java.util.*;

public class Generator {

    public final TickThread thread;
    private final World world;
    private Vector rangeMin;
    private Vector rangeMax;
    private BoundingBox rangeBox;

    private final Map<Long, BlockInfoChunk> blockInfoMap = new HashMap<>();

    public final List<ScriptFunctionHolder> functionList = new ArrayList<>();

    public final Map<String, Object> tag = new HashMap<>();

    public Generator(TickThread thread, World world, Vector rangePos1, Vector rangePos2) {
        this.thread = thread;
        this.world = world;
        this.rangeMin = Vector.getMinimum(rangePos1, rangePos2);
        this.rangeMax = Vector.getMaximum(rangePos1, rangePos2);
        this.rangeBox = BoundingBox.of(rangeMin, rangeMax);
    }

    public @Nullable BlockInfo getBlock(int x, int y, int z) {
        if (y > world.getMaxHeight()) {
            return null;
        }
        BlockInfoChunk chunk = blockInfoMap.computeIfAbsent(ChunkUtil.getChunkKey(x, y), key -> new BlockInfoChunk(thread, world, x >> 4, z >> 4));
        return chunk.getOrCreateBlockInfo(this, new BlockPosition3i(x, y, z));
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
        set.removeIf(blockInfo -> !((Boolean) blockInfo.tag.get("ground")));
        return set;
    }

    public boolean isInRange(int x, int y, int z) {return rangeBox.contains(x, y, z);}

}
