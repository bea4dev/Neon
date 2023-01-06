package com.github.bea4dev.neon.generator;

import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockInfoProvider {

    private static final Map<World, BlockInfoProvider> worldBlockInfoProviderMap = new ConcurrentHashMap<>();

    public static BlockInfoProvider getProvider(World world) {
        return worldBlockInfoProviderMap.computeIfAbsent(world, BlockInfoProvider::new);
    }


    private final World world;
    private final Map<Long, BlockInfoChunk> blockInfoMap = new HashMap<>();

    public BlockInfoProvider(World world) {
        this.world = world;
    }
}
